package memory

import chisel3._
import chisel3.util._

class DMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))      // 访问地址
    val writeData = Input(UInt(32.W)) // 写入数据
    val memWrite = Input(Bool())      // 写使能信号
    val readData = Output(UInt(32.W)) // 读取数据输出
  })

  // 数据存储器，假设大小 256 * 4 = 1024 字节
  val mem = Mem(256, UInt(32.W))

  // 读取数据
  io.readData := mem(io.addr >> 2)

  // 写入数据（仅当 memWrite 为 1 时）
  when(io.memWrite) {
    mem(io.addr >> 2) := io.writeData
  }
}
