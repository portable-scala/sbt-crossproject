object Foo {
  def main(args: Array[String]): Unit =
    require(getClass.getResource("bar") != null)
}
