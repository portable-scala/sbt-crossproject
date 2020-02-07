package sbttest

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello")
    val vmVersion = System.getProperty("java.vm.version")
    println(vmVersion)

    val isScalaJS = 1.0.toString() == "1"
    println(s"isScalaJS = $isScalaJS")
    if (isScalaJS)
      assert(vmVersion == "1.0.0")
  }
}
