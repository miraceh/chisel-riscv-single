package controller

import chisel3._
import chisel3.util._

class Controller extends Module {
  val io = IO(new Bundle {
    val op        = Input(UInt(7.W))   // 指令 opcode
    val funct3    = Input(UInt(3.W))   // funct3
    val funct7b5  = Input(Bool())      // funct7[5]
    val Zero      = Input(Bool())      // 用于 BEQ / BNE 分支计算

    val ResultSrc = Output(UInt(2.W))  // 选择 ALU 结果 or 内存数据
    val MemWrite  = Output(Bool())     // 存储器写使能
    val PCSrc     = Output(Bool())     // 跳转选择
    val ALUSrc    = Output(Bool())     // ALU 操作数选择
    val RegWrite  = Output(Bool())     // 寄存器写使能
    val Jump      = Output(Bool())     // 跳转信号
    val ImmSrc    = Output(UInt(2.W))  // 立即数扩展类型
    val ALUControl = Output(UInt(3.W)) // ALU 操作类型
  })

  // **实例化 Maindec 和 Aludec**
  val maindec = Module(new Maindec())
  val aludec = Module(new Aludec())

  // **连接 Maindec 输入**
  maindec.io.op := io.op

  // **连接 Aludec 输入**
  aludec.io.opb5 := io.op(5)      // 取 opcode[5]
  aludec.io.funct3 := io.funct3
  aludec.io.funct7b5 := io.funct7b5
  aludec.io.ALUOp := maindec.io.ALUOp

  // **Maindec 输出连接到 Controller**
  io.ResultSrc := maindec.io.ResultSrc
  io.MemWrite  := maindec.io.MemWrite
  io.ALUSrc    := maindec.io.ALUSrc
  io.RegWrite  := maindec.io.RegWrite
  io.Jump      := maindec.io.Jump
  io.ImmSrc    := maindec.io.ImmSrc

  // **Aludec 输出连接到 Controller**
  io.ALUControl := aludec.io.ALUControl

  // **PCSrc 计算 (跳转信号)**
  io.PCSrc := (maindec.io.Branch & (io.Zero ^ io.funct3(0))) | maindec.io.Jump
}
