package top

import chisel3._
import chisel3.util._
import core.Core
import memory.IMem
import memory.DMem

class Top extends Module {
  val io = IO(new Bundle {
    val writeData = Output(UInt(32.W)) // 数据存储器写入数据
    val dataAdr   = Output(UInt(32.W)) // 数据存储器地址
    val memWrite  = Output(Bool())     // 数据存储器写使能
  })

  // **实例化 `Core`**
  val core = Module(new Core)

  // **实例化 `IMem`（指令存储器）**
  val imem = Module(new IMem)

  // **实例化 `DMem`（数据存储器）**
  val dmem = Module(new DMem)

  // **连接 `IMem`**
  imem.io.addr := core.io.pc  // PC 作为 `IMem` 访问地址
  core.io.instr := imem.io.inst  // 读取指令

  // **连接 `DMem`**
  dmem.io.addr := core.io.aluResult // `DMem` 访问地址
  dmem.io.memWrite := core.io.memWrite // `DMem` 写使能
  dmem.io.writeData := core.io.writeData // `DMem` 写入数据
  core.io.readData := dmem.io.readData // `DMem` 读取的数据

  // **输出 `Top` 接口**
  io.writeData := core.io.writeData
  io.dataAdr := core.io.aluResult
  io.memWrite := core.io.memWrite
}
