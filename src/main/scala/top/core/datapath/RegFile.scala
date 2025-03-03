package datapath

import chisel3._
import chisel3.util._

class RegFile extends Module {
  val io = IO(new Bundle {
    val rs1       = Input(UInt(5.W))   // 读寄存器 1 地址
    val rs2       = Input(UInt(5.W))   // 读寄存器 2 地址
    val rd        = Input(UInt(5.W))   // 写寄存器地址
    val rdData    = Input(UInt(32.W))  // 要写入的数据
    val regWrite  = Input(Bool())      // 写使能信号

    val rs1Data   = Output(UInt(32.W)) // 读出的寄存器 1 数据
    val rs2Data   = Output(UInt(32.W)) // 读出的寄存器 2 数据
  })

  // **定义 32 个 32-bit 寄存器**
  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // **读寄存器**
  io.rs1Data := Mux(io.rs1 === 0.U, 0.U, regs(io.rs1))  // x0 恒为 0
  io.rs2Data := Mux(io.rs2 === 0.U, 0.U, regs(io.rs2))

  // **写寄存器**
  when(io.regWrite && io.rd =/= 0.U) {  // x0 不能写
    regs(io.rd) := io.rdData
  }
}
