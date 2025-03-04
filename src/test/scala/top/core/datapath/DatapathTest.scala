package top.core.datapath

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class DatapathTest extends AnyFlatSpec with ChiselScalatestTester {
  "Datapath" should "execute ADDI x2, x0, 5 correctly" in {
    test(new Datapath) { c =>
      // **1. 设置 `ADDI x2, x0, 5` 指令**
      c.io.instr.poke("h00500113".U) // ADDI x2, x0, 5
      c.io.immSrc.poke("b00".U)      // I-Type 立即数扩展
      c.io.aluSrc.poke(true.B)       // 选择 `ImmExt` 作为 `SrcB`
      c.io.aluControl.poke("b000".U) // ALU 执行 `ADD`
      c.io.regWrite.poke(true.B)     // 允许写入寄存器
      c.io.resultSrc.poke(0.U)       // `ALUResult` 作为 `rd` 写入值

      c.clock.step(1) // **执行指令**

      // **2. 检查 `ImmExt` 是否正确**
      val immExt = c.io.aluResult.peek().litValue.toInt
      assert(immExt == 5, s"Expected ImmExt = 5, but got $immExt")

      // **3. 检查 `ALUResult` 是否正确**
      val aluRes = c.io.aluResult.peek().litValue.toInt
      assert(aluRes == 5, s"Expected ALUResult = 5, but got $aluRes")

      // **4. 直接 `peek` `debugResult`，检查 `x2` 是否等于 `5`**
      val regX2 = c.io.debugResult.peek().litValue.toInt
      assert(regX2 == 5, s"Expected x2 = 5, but got $regX2")

      println("✅ ADDI x2, x0, 5 executed correctly!")
    }
  }
}
