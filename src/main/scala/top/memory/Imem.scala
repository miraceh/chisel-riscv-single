package memory

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class IMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))    // PC 地址输入
    val inst = Output(UInt(32.W))   // 读取的指令
  })

  // 指令存储器，假设大小 256 * 4 = 1024 字节
  val mem = Mem(256, UInt(32.W))

  // 读取 mem.hex 预加载指令
  loadMemoryFromFile(mem, "resource/test2.hex")

  // 读取指令（按字对齐）
  io.inst := mem(io.addr >> 2)
}
