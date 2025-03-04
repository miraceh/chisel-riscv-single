package top.core.controller

import chisel3._
import chisel3.util._

class Aludec extends Module {
  val io = IO(new Bundle {
    val opb5       = Input(Bool())      // opcode[5]，区分 R-type/I-type
    val funct3     = Input(UInt(3.W))   // funct3
    val funct7b5   = Input(Bool())      // funct7[5]
    val ALUOp      = Input(UInt(2.W))   // 来自 Maindec 的 ALU 操作类型
    val ALUControl = Output(UInt(3.W))  // 输出 ALU 控制信号
  })

  // 默认 ALUControl 为 ADD
  io.ALUControl := "b000".U

  // 计算 R-Type SUB 需要的标志位
  val RtypeSub = io.funct7b5 & io.opb5

  switch(io.ALUOp) {
    is("b00".U) { io.ALUControl := "b000".U } // LW / SW → ADD
    is("b01".U) { io.ALUControl := "b001".U } // BEQ / BNE → SUB
  }

  // **合并 ALUOp = 10 和 11 的逻辑**
  when(io.ALUOp === "b10".U || io.ALUOp === "b11".U) {
    switch(io.funct3) {
      is("b000".U) { io.ALUControl := Mux(RtypeSub, "b001".U, "b000".U) } // SUB / ADD / ADDI
      is("b001".U) { io.ALUControl := "b110".U } // SLL / SLLI
      is("b010".U) { io.ALUControl := "b101".U } // SLT / SLTI
      is("b100".U) { io.ALUControl := "b100".U } // XOR / XORI
      is("b101".U) { io.ALUControl := "b111".U } // SRL / SRLI
      is("b110".U) { io.ALUControl := "b011".U } // OR / ORI
      is("b111".U) { io.ALUControl := "b010".U } // AND / ANDI
    }
  }
}
