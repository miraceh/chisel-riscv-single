package top.core.datapath

import chisel3._
import chisel3.util._

class ALU extends Module {
  val io = IO(new Bundle {
    val srcA       = Input(UInt(32.W))  // 操作数 A
    val srcB       = Input(UInt(32.W))  // 操作数 B
    val ALUControl = Input(UInt(3.W))   // ALU 控制信号
    val ALUResult  = Output(UInt(32.W)) // 计算结果
    val Zero       = Output(Bool())     // Zero 标志
  })

  // **CondInvB (用于 SUB)**
  val condInvB = Mux(io.ALUControl(0), ~io.srcB, io.srcB)
  val sum = io.srcA + condInvB + io.ALUControl(0)

  // **SLT 计算所需的 Overflow**
  val isAddSub = ~io.ALUControl(2) & ~io.ALUControl(1) | ~io.ALUControl(1) & io.ALUControl(0)
  val overflow = ~(io.ALUControl(0) ^ io.srcA(31) ^ io.srcB(31)) &
                 (io.srcA(31) ^ sum(31)) & isAddSub

  // **SLT 需要用 SInt 进行有符号比较**
  val sltResult = (io.srcA.asSInt < io.srcB.asSInt).asUInt

  // **修正 MuxLookup 语法**
  io.ALUResult := MuxLookup(io.ALUControl, 0.U)(Seq(
    "b000".U -> sum,               // ADD
    "b001".U -> sum,               // SUB
    "b010".U -> (io.srcA & io.srcB),  // AND
    "b011".U -> (io.srcA | io.srcB),  // OR
    "b100".U -> (io.srcA ^ io.srcB),  // XOR
    "b101".U -> sltResult,         // SLT (带符号比较)
    "b110".U -> (io.srcA << io.srcB(4, 0)), // SLL
    "b111".U -> (io.srcA >> io.srcB(4, 0))  // SRL
  ))

  // **Zero 标志**
  io.Zero := io.ALUResult === 0.U
}
