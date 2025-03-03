package memory

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class DMemTest extends AnyFlatSpec with ChiselScalatestTester {
  "DMem" should "write and read correctly" in {
    test(new DMem) { c =>
      
      def writeMem(addr: Int, data: BigInt): Unit = {
        c.io.addr.poke(addr.U)
        c.io.writeData.poke((data & 0xFFFFFFFFL).U) // 转为无符号 32-bit
        c.io.memWrite.poke(true.B)
        c.clock.step(1)
        c.io.memWrite.poke(false.B) // 关闭写使能
      }

      def readMem(addr: Int): Unit = {
        c.io.addr.poke(addr.U)
        c.clock.step(1)
        println(f"Read @ 0x$addr%02X: 0x${c.io.readData.peek().litValue}%08X")
      }

      // 写入数据
      writeMem(0x00, 0xDEADBEEF) // 存入 0xDEADBEEF
      writeMem(0x04, 0x12345678) // 存入 0x12345678

      // 读取数据
      readMem(0x00) // 预期: 0xDEADBEEF
      readMem(0x04) // 预期: 0x12345678
      readMem(0x08) // 预期: 0x00000000 (未写入)
    }
  }
}
