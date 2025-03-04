package top.core.datapath

import chisel3._
import chisel3.util._

class Datapath extends Module {
  val io = IO(new Bundle {
    val instr     = Input(UInt(32.W))   // 指令输入
    val immSrc    = Input(UInt(2.W))    // 立即数类型
    val pcSrc     = Input(Bool())       // 控制信号：是否跳转
    val pcOut     = Output(UInt(32.W))  // 输出当前 PC

    val aluSrc    = Input(Bool())       // 控制信号：ALU 选择操作数
    val aluControl = Input(UInt(3.W))   // 控制信号：ALU 控制
    val aluResult = Output(UInt(32.W))  // ALU 计算结果
    val zero      = Output(Bool())      // ALU Zero 信号

    val writeData = Output(UInt(32.W))  // **存储器写入数据**
    val readData  = Input(UInt(32.W))   // **存储器读取数据**
    val resultSrc = Input(UInt(2.W))    // **控制 `Result` 选择来源**
    val regWrite  = Input(Bool())       // **寄存器写使能**

    val debugResult = Output(UInt(32.W)) // 添加调试端口

  })

  // **实例化 `ImmExtend`**
  val immExtender = Module(new ImmExtend)
  immExtender.io.instr := io.instr
  immExtender.io.immSrc := io.immSrc
  val immExtReg = immExtender.io.immExt // 存储扩展后的立即数

  // **PC 逻辑**
  val pcReg = RegInit(0.U(32.W))       // PC 寄存器，初始化为 0
  val pcPlus4 = pcReg + 4.U            // PC + 4
  val pcTarget = pcReg + immExtReg      // 跳转地址

  val pcNext = Mux(io.pcSrc, pcTarget, pcPlus4) // 选择下一个 PC
  pcReg := pcNext  // **更新 PC**
  io.pcOut := pcReg  // **输出当前 PC**

  // **实例化 `RegFile`**
  val regFile = Module(new RegFile)
  
  // **连接 `RegFile`**
  regFile.io.rs1 := io.instr(19, 15)  // 读寄存器 1
  regFile.io.rs2 := io.instr(24, 20)  // 读寄存器 2

  val srcA = regFile.io.rs1Data  // 读出的寄存器值
  val writeDataReg = regFile.io.rs2Data // 读出的寄存器值（写入内存时用）

  // **SrcB 选择**
  val srcB = Mux(io.aluSrc, immExtReg, writeDataReg)  // 选择 `WriteData` 或 `ImmExt`

  // **实例化 `ALU`**
  val alu = Module(new ALU)

  // **连接 `ALU`**
  alu.io.srcA := srcA
  alu.io.srcB := srcB
  alu.io.ALUControl := io.aluControl

  // **输出 ALU 计算结果**
  io.aluResult := alu.io.ALUResult
  io.zero := alu.io.Zero

  // **输出 `WriteData`**
  io.writeData := writeDataReg

  // **选择 `Result` 来源**
// **选择 `Result` 来源**
val result = MuxLookup(io.resultSrc, 0.U)(Seq(
  0.U -> io.aluResult,  // 计算结果
  1.U -> io.readData,   // 访存加载
  2.U -> pcPlus4        // PC + 4（跳转指令）
))

  // **连接 `RegFile` 写端口**
  regFile.io.rd := io.instr(11, 7)    // **写入目标寄存器**
  regFile.io.regWrite := io.regWrite  // **写使能信号**
  regFile.io.rdData := result      // **要写入 `RegFile` 的数据**


  io.debugResult := result
  // **调试信息: 每个时钟周期打印**
  printf("PC: 0x%x | Instr: 0x%x\n", pcReg, io.instr)
  printf("RegFile: rs1=x%x (%d), rs2=x%x (%d), rd=x%x\n", io.instr(19, 15), srcA, io.instr(24, 20), writeDataReg, io.instr(11, 7))
  printf("ImmExt: %d\n", immExtReg)
  printf("ALU: srcA=%d, srcB=%d, aluResult=%d, zero=%d\n", srcA, srcB, io.aluResult, io.zero)
  printf("RegWrite: %d, WriteData=%d, ReadData=%d\n", io.regWrite, io.writeData, io.readData)
  printf("WB: Result=%d (from source %d)\n", result, io.resultSrc)
  printf("--------------------------------------------------\n")

}
