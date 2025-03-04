package datapath

import chisel3._
import chisel3.util._

class ImmExtend extends Module {
  val io = IO(new Bundle {
    val instr  = Input(UInt(32.W))   // 指令输入
    val immSrc = Input(UInt(2.W))    // 立即数类型
    val immExt = Output(UInt(32.W))  // 扩展后的立即数
  })

  // **立即数扩展逻辑**
  io.immExt := MuxLookup(io.immSrc, 0.S(32.W))(
    Seq(
      "b00".U -> Cat(Fill(20, io.instr(31)), io.instr(31, 20)).asSInt, // I 型
      "b01".U -> Cat(Fill(20, io.instr(31)), io.instr(31, 25), io.instr(11, 7)).asSInt, // S 型
      "b10".U -> Cat(Fill(20, io.instr(31)), io.instr(7), io.instr(30, 25), io.instr(11, 8), 0.U(1.W)).asSInt, // B 型
      "b11".U -> Cat(Fill(12, io.instr(31)), io.instr(19, 12), io.instr(20), io.instr(30, 21), 0.U(1.W)).asSInt // J 型
    )
  ).asUInt // **确保最终输出是 UInt**
}
