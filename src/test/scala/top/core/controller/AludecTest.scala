package top.core.controller

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class AludecTest extends AnyFlatSpec with ChiselScalatestTester {
  "Aludec" should "generate correct ALUControl signals" in {
    test(new Aludec) { c =>

      def testALU(opb5: Boolean, funct3: Int, funct7b5: Boolean, ALUOp: Int, expected: Int): Unit = {
        c.io.opb5.poke(opb5.B)
        c.io.funct3.poke(funct3.U)
        c.io.funct7b5.poke(funct7b5.B)
        c.io.ALUOp.poke(ALUOp.U)
        c.clock.step(1)

        val aluControl = c.io.ALUControl.peek().litValue
        val aluOpStr = ALUOp.toBinaryString.reverse.padTo(2, '0').reverse
        val funct3Str = funct3.toBinaryString.reverse.padTo(3, '0').reverse
        val funct7b5Str = if (funct7b5) "1" else "0"
        val aluControlStr = aluControl.toLong.toBinaryString.reverse.padTo(3, '0').reverse

        println(s"ALUOp: $aluOpStr, funct3: $funct3Str, funct7b5: $funct7b5Str -> ALUControl: $aluControlStr")
        assert(aluControl == expected, s"Expected ALUControl: $expected, got: $aluControl")
      }

      // 测试 LW / SW (ALUOp = 00, ALUControl = 000)
      testALU(opb5 = false, funct3 = 0, funct7b5 = false, ALUOp = 0, expected = 0)

      // 测试 BEQ / BNE (ALUOp = 01, ALUControl = 001)
      testALU(opb5 = false, funct3 = 0, funct7b5 = false, ALUOp = 1, expected = 1)

      // 测试 R-Type ADD (ALUOp = 10, funct3 = 000, funct7b5 = 0, ALUControl = 000)
      testALU(opb5 = true, funct3 = 0, funct7b5 = false, ALUOp = 2, expected = 0)

      // 测试 R-Type SUB (ALUOp = 10, funct3 = 000, funct7b5 = 1, ALUControl = 001)
      testALU(opb5 = true, funct3 = 0, funct7b5 = true, ALUOp = 2, expected = 1)

      // 测试 `ALUOp = 11` 是否与 `ALUOp = 10` 产生相同结果
      testALU(opb5 = true, funct3 = 0, funct7b5 = false, ALUOp = 3, expected = 0) // ADD
      testALU(opb5 = true, funct3 = 0, funct7b5 = true, ALUOp = 3, expected = 1) // SUB

      // 测试 SLL (ALUOp = 10, funct3 = 001, ALUControl = 110)
      testALU(opb5 = false, funct3 = 1, funct7b5 = false, ALUOp = 2, expected = 6)

      // 测试 SLT (ALUOp = 10, funct3 = 010, ALUControl = 101)
      testALU(opb5 = false, funct3 = 2, funct7b5 = false, ALUOp = 2, expected = 5)

      // 测试 XOR (ALUOp = 10, funct3 = 100, ALUControl = 100)
      testALU(opb5 = false, funct3 = 4, funct7b5 = false, ALUOp = 2, expected = 4)

      // 测试 SRL (ALUOp = 10, funct3 = 101, ALUControl = 111)
      testALU(opb5 = false, funct3 = 5, funct7b5 = false, ALUOp = 2, expected = 7)

      // 测试 OR (ALUOp = 10, funct3 = 110, ALUControl = 011)
      testALU(opb5 = false, funct3 = 6, funct7b5 = false, ALUOp = 2, expected = 3)

      // 测试 AND (ALUOp = 10, funct3 = 111, ALUControl = 010)
      testALU(opb5 = false, funct3 = 7, funct7b5 = false, ALUOp = 2, expected = 2)

      // 确保 ALUOp = 11 也能正确执行
      testALU(opb5 = false, funct3 = 7, funct7b5 = false, ALUOp = 3, expected = 2) // AND

      println("✅ Passed all Aludec tests")
    }
  }
}
