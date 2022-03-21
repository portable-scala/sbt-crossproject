import Platform._
object Foo {
  def main(args: Array[String]): Unit = {
    assert(List(isJVM, isJS, isNative).count(identity) == 1)

    if (isJVM)
      assert(JSOrJVM.check && JVMOrNative.check && !JSOrNative.check)
    if (isJS)
      assert(JSOrJVM.check && !JVMOrNative.check && JSOrNative.check)
    if (isNative)
      assert(!JSOrJVM.check && JVMOrNative.check && JSOrNative.check)
  }
}
