package datapath

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class DatapathTest extends AnyFlatSpec with ChiselScalatestTester {
  "Datapath" should "update PC correctly" in {
    test(new Datapath) { c =>

      def stepAndCheck(expectedPC: Long) = {
        c.clock.step(1)
        val pcOut = c.io.pcOut.peek().litValue.toLong
        println(s"PC: 0x${pcOut.toHexString.toUpperCase}")
        assert(pcOut == expectedPC, s"Expected PC: 0x${expectedPC.toHexString}, but got: 0x${pcOut.toHexString}")
      }

      // **1. 顺序执行 PC = PC + 4**
      println("=== Sequential Execution ===")
      c.io.pcSrc.poke(false.B) // 不跳转
      stepAndCheck(0x4)
      stepAndCheck(0x8)
      stepAndCheck(0xC)

      // **2. 测试 PC 跳转**
      println("=== Branch Execution ===")
      c.io.pcSrc.poke(true.B) // 触发跳转
      c.io.instr.poke(0x00008067.U) // 模拟 JAL 指令
      c.io.immSrc.poke(3.U) // J 型立即数扩展
      c.clock.step(1) // 让 ImmExt 更新
      stepAndCheck(0x800C) // 目标地址 = 0xC + 0x8000（假设 ImmExt = 0x8000）

      // **3. 继续顺序执行**
      c.io.pcSrc.poke(false.B) // 取消跳转
      stepAndCheck(0x8010)
      stepAndCheck(0x8014)

      println("✅ Passed all PC logic tests")
    }
  }
}
