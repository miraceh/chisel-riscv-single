package datapath

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ALUTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU" should "perform operations correctly" in {
    test(new ALU) { c =>

      def testALU(srcA: Long, srcB: Long, aluControl: Int, expected: Long, expectedZero: Boolean) = {
        c.io.srcA.poke((srcA & 0xFFFFFFFFL).U)
        c.io.srcB.poke((srcB & 0xFFFFFFFFL).U)
        c.io.ALUControl.poke(aluControl.U)
        c.clock.step(1)
        val result = c.io.ALUResult.peek().litValue
        val zero = c.io.Zero.peek().litToBoolean

        println(s"ALUControl: ${aluControl.toBinaryString.reverse.padTo(3, '0').reverse}, " +
                s"srcA: 0x${srcA.toHexString.toUpperCase}, " +
                s"srcB: 0x${srcB.toHexString.toUpperCase} -> " +
                s"Result: 0x${result.toLong.toHexString.toUpperCase}, Zero: $zero")

        assert(result == (expected & 0xFFFFFFFFL), s"Expected ALUResult: ${expected.toLong.toHexString}, got: ${result.toLong.toHexString}")
        assert(zero == expectedZero, s"Expected Zero: $expectedZero, got: $zero")
      }

      // **测试 ADD (000)**
      testALU(10, 20, 0, 30, false)
      testALU(0xFFFFFFF6L, 10, 0, 0, true) // -10 + 10 = 0

      // **测试 SUB (001)**
      testALU(30, 20, 1, 10, false)
      testALU(10, 10, 1, 0, true)

      // **测试 AND (010)**
      testALU(0xF0F0F0F0, 0x0F0F0F0F, 2, 0x00000000, true)
      testALU(0xFFFFFF00, 0x12345678, 2, 0x12345600, false)

      // **测试 OR (011)**
      testALU(0xF0F0F0F0, 0x0F0F0F0F, 3, 0xFFFFFFFF, false)

      // **测试 XOR (100)**
      testALU(0b1100, 0b1010, 4, 0b0110, false)

      // **测试 SLT (101)**
      testALU(0xFFFFFFFBL, 3, 5, 1, false)  // -5 < 3 -> 1
      testALU(3, 0xFFFFFFFBL, 5, 0, true)   // 3 >= -5 -> 0

      // **测试 SLL (110)**
      testALU(0x1, 2, 6, 0x4, false)  // 1 << 2 = 4

      // **测试 SRL (111)**
      testALU(0x8, 2, 7, 0x2, false)  // 8 >> 2 = 2

      println("✅ Passed all ALU tests")
    }
  }
}
