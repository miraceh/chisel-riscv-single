package top.core

import chisel3._
import chisel3.util._
import top.core.controller.Controller
import top.core.datapath.Datapath 

class Core extends Module {
  val io = IO(new Bundle {
    val instr     = Input(UInt(32.W))    // 指令
    val readData  = Input(UInt(32.W))    // 从存储器读取的数据

    val pc        = Output(UInt(32.W))   // 当前 PC
    val memWrite  = Output(Bool())       // 存储器写使能
    val aluResult = Output(UInt(32.W))   // ALU 计算结果
    val writeData = Output(UInt(32.W))   // 存储器写入数据
  })

  // **实例化 `Controller`**
  val controller = Module(new Controller)
  controller.io.op := io.instr(6, 0)
  controller.io.funct3 := io.instr(14, 12)
  controller.io.funct7b5 := io.instr(30)

  // **实例化 `Datapath`**
  val datapath = Module(new Datapath)

  // **连接 `Datapath` 和 `Controller`**
  datapath.io.instr := io.instr
  datapath.io.immSrc := controller.io.ImmSrc
  datapath.io.pcSrc := controller.io.PCSrc
  datapath.io.aluSrc := controller.io.ALUSrc
  datapath.io.aluControl := controller.io.ALUControl
  datapath.io.resultSrc := controller.io.ResultSrc
  datapath.io.regWrite := controller.io.RegWrite

  // **连接 `Zero` 信号**
  controller.io.Zero := datapath.io.zero

  // **连接 `Datapath` 存储器相关信号**
  datapath.io.readData := io.readData
  io.writeData := datapath.io.writeData
  io.aluResult := datapath.io.aluResult

  // **连接 `PC`**
  io.pc := datapath.io.pcOut

  // **存储器写使能信号**
  io.memWrite := controller.io.MemWrite
}
