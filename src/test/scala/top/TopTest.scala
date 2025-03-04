package top

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class TopTest extends AnyFlatSpec with ChiselScalatestTester {
  "Top" should "execute test1.hex correctly" in {
    test(new Top) { c =>
    //   // **复位逻辑**
    //   c.reset.poke(true.B)
    //   c.clock.step(5) // 复位一段时间
    //   c.reset.poke(false.B)

      // **运行处理器，检查存储器写入**
      for (_ <- 0 until 25) { // 限制最大运行周期，避免死循环
        c.clock.step(1)

        if (c.io.memWrite.peek().litToBoolean) {
          val dataAdr = c.io.dataAdr.peek().litValue.toInt
          val writeData = c.io.writeData.peek().litValue.toInt

          if (dataAdr == 100 && writeData == 25) {
            println("-------Test1 Simulation succeeded-------")
            assert(true) // 测试通过
            // return  // **成功后立即退出**
            sys.exit(0)
          } else if (dataAdr != 96) {
            println("Simulation failed")
            assert(false, s"Unexpected memory write: Addr = $dataAdr, Data = $writeData")
          }
        }
      }
      assert(false, "Test did not complete successfully") // **如果100周期后仍未成功，测试失败**
    }
  }
}

class TopTest2 extends AnyFlatSpec with ChiselScalatestTester {
  "Top" should "execute test2.hex correctly" in {
    test(new Top) { c =>
    //   // **复位逻辑**
    //   c.reset.poke(true.B)
    //   c.clock.step(5) // 复位一段时间
    //   c.reset.poke(false.B)

      // **运行处理器，检查存储器写入**
      for (_ <- 0 until 200) { // 限制最大运行周期，避免死循环
        c.clock.step(1)

        if (c.io.memWrite.peek().litToBoolean) {
          val dataAdr = c.io.dataAdr.peek().litValue.toInt
          val writeData = c.io.writeData.peek().litValue.toInt

          if (dataAdr == 216 && writeData == 4140) {
            println("-------Test2 Simulation succeeded-------")
            assert(true) // 测试通过
            sys.exit(0)
          }
        }
      }
      assert(false, "Test did not complete successfully") // **如果100周期后仍未成功，测试失败**
    }
  }
}
