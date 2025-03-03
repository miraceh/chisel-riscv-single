package controller

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MaindecTest extends AnyFlatSpec with ChiselScalatestTester {
  "Maindec" should "generate correct control signals" in {
    test(new Maindec) { c =>

      def testOpcode(opcode: String, expected: (String, Boolean, Boolean, Boolean, Boolean, Boolean, String, String)) = {
        c.io.op.poke(BigInt(opcode, 2).U) // 先转换成 BigInt，再转换成 UInt
        c.clock.step(1)
        println(f"Opcode: 0b$opcode")

        // 读取信号
        val resultSrc = c.io.ResultSrc.peek().litValue
        val memWrite  = c.io.MemWrite.peek().litToBoolean
        val aluSrc    = c.io.ALUSrc.peek().litToBoolean
        val regWrite  = c.io.RegWrite.peek().litToBoolean
        val jump      = c.io.Jump.peek().litToBoolean
        val branch    = c.io.Branch.peek().litToBoolean
        val immSrc    = c.io.ImmSrc.peek().litValue
        val aluOp     = c.io.ALUOp.peek().litValue

        println(f"  ResultSrc: $resultSrc%02X, MemWrite: $memWrite, ALUSrc: $aluSrc, RegWrite: $regWrite, Jump: $jump, Branch: $branch, ImmSrc: $immSrc%02X, ALUOp: $aluOp%02X")

        // 断言
        assert(resultSrc == BigInt(expected._1, 2))
        assert(memWrite  == expected._2)
        assert(aluSrc    == expected._3)
        assert(regWrite  == expected._4)
        assert(jump      == expected._5)
        assert(branch    == expected._6)
        assert(immSrc    == BigInt(expected._7, 2))
        assert(aluOp     == BigInt(expected._8, 2))
      }

      // 测试不同的 opcode
      testOpcode("0000011", ("01", false, true, true, false, false, "00", "00")) // LW
      testOpcode("0100011", ("00", true, true, false, false, false, "01", "00")) // SW
      testOpcode("1100011", ("00", false, false, false, false, true, "10", "01")) // BEQ/BNE
      testOpcode("1101111", ("10", false, true, true, true, false, "11", "00")) // JAL
      testOpcode("0010011", ("00", false, true, true, false, false, "00", "10")) // I-Type ALU
      testOpcode("0110011", ("00", false, false, true, false, false, "00", "10")) // R-Type ALU
      testOpcode("1111111", ("00", false, false, false, false, false, "00", "00")) // 默认
    }
  }
}
