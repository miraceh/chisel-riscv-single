package datapath

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RegFileTest extends AnyFlatSpec with ChiselScalatestTester {
  "RegFile" should "read and write registers correctly" in {
    test(new RegFile) { c =>
      
      def writeReg(rd: Int, data: Int, regWrite: Boolean = true) = {
        c.io.rd.poke(rd.U)
        c.io.rdData.poke(data.U)
        c.io.regWrite.poke(regWrite.B)
        c.clock.step(1)
      }

      def readReg(rs1: Int, rs2: Int) = {
        c.io.rs1.poke(rs1.U)
        c.io.rs2.poke(rs2.U)
        c.clock.step(1)
        val rs1Data = c.io.rs1Data.peek().litValue
        val rs2Data = c.io.rs2Data.peek().litValue
        println(f"Read rs1[$rs1]: 0x$rs1Data%08X, rs2[$rs2]: 0x$rs2Data%08X")
        (rs1Data, rs2Data)
      }

      // **测试 x0 恒为 0**
      writeReg(0, 123) // 尝试写入 x0
      val (x0Val, _) = readReg(0, 1)
      assert(x0Val == 0, "x0 should always be 0!")

      // **测试写入 & 读取**
      writeReg(1, 42)  // x1 = 42
      writeReg(2, 99)  // x2 = 99
      val (x1Val, x2Val) = readReg(1, 2)
      assert(x1Val == 42, "x1 should be 42")
      assert(x2Val == 99, "x2 should be 99")

      // **测试多个寄存器写入 & 读取**
      writeReg(3, 12345)
      writeReg(4, 67890)
      val (x3Val, x4Val) = readReg(3, 4)
      assert(x3Val == 12345, "x3 should be 12345")
      assert(x4Val == 67890, "x4 should be 67890")

      println("✅ Passed all RegFile tests")
    }
  }
}
