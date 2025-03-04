package top.core.controller

import chisel3._
import chisel3.util._

class Maindec extends Module {
  val io = IO(new Bundle {
    val op        = Input(UInt(7.W))   // 指令 opcode
    val ResultSrc = Output(UInt(2.W))  // 选择 ALU 结果 or 内存数据
    val MemWrite  = Output(Bool())     // 存储器写使能
    val ALUSrc    = Output(Bool())     // ALU 操作数选择
    val RegWrite  = Output(Bool())     // 寄存器写使能
    val Jump      = Output(Bool())     // 跳转信号
    val Branch    = Output(Bool())     // 分支信号 (BEQ, BNE)
    val ImmSrc    = Output(UInt(2.W))  // 立即数扩展类型
    val ALUOp     = Output(UInt(2.W))  // ALU 操作选择
  })

  // 默认值
  io.ResultSrc := 0.U
  io.MemWrite  := false.B
  io.ALUSrc    := false.B
  io.RegWrite  := false.B
  io.Jump      := false.B
  io.Branch    := false.B
  io.ImmSrc    := 0.U
  io.ALUOp     := 0.U

  switch(io.op) {
    is("b0000011".U) { // LW
      io.ResultSrc := "b01".U
      io.ALUSrc    := true.B
      io.RegWrite  := true.B
      io.ImmSrc    := "b00".U
      io.ALUOp     := "b00".U
    }
    is("b0100011".U) { // SW
      io.MemWrite  := true.B
      io.ALUSrc    := true.B
      io.ImmSrc    := "b01".U
      io.ALUOp     := "b00".U
    }
    is("b1100011".U) { // BEQ/BNE (Branch)
      io.Branch    := true.B
      io.ImmSrc    := "b10".U
      io.ALUOp     := "b01".U
    }
    is("b1101111".U) { // JAL
      io.ResultSrc := "b10".U
      io.ALUSrc    := true.B
      io.RegWrite  := true.B
      io.Jump      := true.B
      io.ImmSrc    := "b11".U
    }
    is("b0010011".U) { // I-Type ALU
      io.ALUSrc    := true.B
      io.RegWrite  := true.B
      io.ALUOp     := "b10".U
    }
    is("b0110011".U) { // R-Type ALU
      io.RegWrite  := true.B
      io.ALUOp     := "b10".U
    }
  }
}
