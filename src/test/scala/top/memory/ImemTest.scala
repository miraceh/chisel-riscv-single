package memory

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class IMemTest extends AnyFlatSpec with ChiselScalatestTester {
  "IMem" should "fetch correct instruction" in {
    test(new IMem) { c =>
      def printHex(addr: Int): Unit = {
        c.io.addr.poke(addr.U)
        c.clock.step(1)
        println(f"Instr @ 0x$addr%02X: 0x${c.io.inst.peek().litValue}%08X")
      }

      printHex(0x00)  // 访问 mem(0)
      printHex(0x04)  // 访问 mem(1)
      printHex(0x08)  // 访问 mem(2)
      printHex(0x0C)  // 访问 mem(3)
    }
  }
}
