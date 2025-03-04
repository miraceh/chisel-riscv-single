package top.core.datapath

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ImmExtendTest extends AnyFlatSpec with ChiselScalatestTester {
  "ImmExtend" should "extend immediates correctly" in {
    test(new ImmExtend) { c =>

      def signExtend(value: Long): BigInt = {
        if (value < 0) (BigInt(1) << 32) + value else BigInt(value)
      }

      def testImm(instr: Long, immSrc: Int, expected: Long) = {
        c.io.instr.poke(signExtend(instr).U)
        c.io.immSrc.poke(immSrc.U)
        c.clock.step(1)
        val immExt = c.io.immExt.peek().litValue

        println(s"Instr: 0x${instr.toHexString.toUpperCase}, " +
                s"immSrc: ${immSrc.toBinaryString.reverse.padTo(2, '0').reverse} -> " +
                s"ImmExt: 0x${immExt.toLong.toHexString.toUpperCase}")

        assert(immExt == (expected & 0xFFFFFFFFL), s"Expected: ${expected.toHexString}, got: ${immExt.toLong.toHexString}")
      }

      // **测试 I 型指令**
      testImm(0x00500113, 0, 0x00000005) // imm = 0xF
    //   testImm(0xFFF50613, 0, 0xFFFFFFFF) // imm = -1 (符号扩展)

    //   // **测试 S 型指令**
    //   testImm(0x00A28223, 1, 0x00000004) // imm = 0xA
    //   testImm(0xFEA28223, 1, 0xFFFFFFE4) // imm = -28 (符号扩展)

    //   // **测试 B 型指令**
    //   testImm(0xFE059AE3, 2, 0xFFFFFFF4) // imm = -12
    //   testImm(0x0A059AE3, 2, 0x000008B4) // imm = 2228

    //   // **测试 J 型指令**
    //   testImm(0x8000006F, 3, 0xFFF00000) // 
    //   testImm(0x0040006F, 3, 0x00000004) // imm = 4

      println("✅ Passed all ImmExtend tests")
    }
  }
}
