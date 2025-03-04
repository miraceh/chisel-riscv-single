package datapath

import chisel3._
import chisel3.util._

class Datapath extends Module {
  val io = IO(new Bundle {
    val instr  = Input(UInt(32.W))   // 指令输入
    val immSrc = Input(UInt(2.W))    // 立即数类型
    val pcSrc  = Input(Bool())       // 控制信号：是否跳转
    val pcOut  = Output(UInt(32.W))  // 输出当前 PC
  })

  // **实例化 `ImmExtend`**
  val immExtender = Module(new ImmExtend)
  immExtender.io.instr := io.instr
  immExtender.io.immSrc := io.immSrc
  val immExtReg = RegNext(immExtender.io.immExt) // 存储扩展后的立即数

  // **PC 逻辑**
  val pcReg = RegInit(0.U(32.W))       // PC 寄存器，初始化为 0
  val pcPlus4 = pcReg + 4.U            // PC + 4
  val pcTarget = pcReg + immExtReg      // 跳转地址

  val pcNext = Mux(io.pcSrc, pcTarget, pcPlus4) // 选择下一个 PC
  pcReg := pcNext  // **更新 PC**

  io.pcOut := pcReg  // **输出当前 PC**
}
