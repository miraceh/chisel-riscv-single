package controller

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ControllerTest extends AnyFlatSpec with ChiselScalatestTester {
  "Controller" should "generate correct control signals" in {
    test(new Controller) { c =>

      def testControl(opcode: String, funct3: Int, funct7b5: Boolean, Zero: Boolean, 
                      expected: (String, Boolean, Boolean, Boolean, Boolean, Boolean, String, String, Boolean)) = {
        c.io.op.poke(BigInt(opcode, 2).U)
        c.io.funct3.poke(funct3.U)
        c.io.funct7b5.poke(funct7b5.B)
        c.io.Zero.poke(Zero.B)
        c.clock.step(1)

        val resultSrc = c.io.ResultSrc.peek().litValue
        val memWrite  = c.io.MemWrite.peek().litToBoolean
        val aluSrc    = c.io.ALUSrc.peek().litToBoolean
        val regWrite  = c.io.RegWrite.peek().litToBoolean
        val jump      = c.io.Jump.peek().litToBoolean
        val branch    = c.io.PCSrc.peek().litToBoolean
        val immSrc    = c.io.ImmSrc.peek().litValue
        val aluControl = c.io.ALUControl.peek().litValue

        // **转换二进制字符串**
        val opcodeStr = opcode
        val funct3Str = funct3.toBinaryString.reverse.padTo(3, '0').reverse
        val funct7b5Str = if (funct7b5) "1" else "0"
        val immSrcStr = immSrc.toLong.toBinaryString.reverse.padTo(2, '0').reverse
        val aluControlStr = aluControl.toLong.toBinaryString.reverse.padTo(3, '0').reverse

        println(s"Opcode: 0b$opcodeStr, funct3: $funct3Str, funct7b5: $funct7b5Str -> " +
          s"ResultSrc: $resultSrc, MemWrite: $memWrite, ALUSrc: $aluSrc, RegWrite: $regWrite, " +
          s"Jump: $jump, Branch: $branch, ImmSrc: $immSrcStr, ALUControl: $aluControlStr")

        assert(resultSrc == BigInt(expected._1, 2))
        assert(memWrite  == expected._2)
        assert(aluSrc    == expected._3)
        assert(regWrite  == expected._4)
        assert(jump      == expected._5)
        assert(branch    == expected._6)
        assert(immSrc    == BigInt(expected._7, 2))
        assert(aluControl == BigInt(expected._8, 2))
        assert(branch    == expected._9)
      }

      // 测试 LW 指令 (opcode = 0000011, funct3 = 000)
      testControl("0000011", 0, false, false, ("01", false, true, true, false, false, "00", "000", false))

      // 测试 SW 指令 (opcode = 0100011, funct3 = 000)
      testControl("0100011", 0, false, false, ("00", true, true, false, false, false, "01", "000", false))

      // 测试 BEQ (opcode = 1100011, funct3 = 000, Zero=1 -> 跳转)
      testControl("1100011", 0, false, true, ("00", false, false, false, false, true, "10", "001", true))

      // 测试 BNE (opcode = 1100011, funct3 = 001, Zero=1 -> 不跳转)
      testControl("1100011", 1, false, true, ("00", false, false, false, false, false, "10", "001", false))

      // 测试 JAL (opcode = 1101111, funct3 = XXX)
      testControl("1101111", 0, false, false, ("10", false, true, true, true, true, "11", "000", true))

      // 测试 I-Type ALU (opcode = 0010011, funct3 = 000, ADDI)
      testControl("0010011", 0, false, false, ("00", false, true, true, false, false, "00", "000", false))

      // 测试 R-Type ADD (opcode = 0110011, funct3 = 000, funct7b5 = 0)
      testControl("0110011", 0, false, false, ("00", false, false, true, false, false, "00", "000", false))

      // 测试 R-Type SUB (opcode = 0110011, funct3 = 000, funct7b5 = 1)
      testControl("0110011", 0, true, false, ("00", false, false, true, false, false, "00", "001", false))

      println("✅ Passed all Controller tests")
    }
  }
}
