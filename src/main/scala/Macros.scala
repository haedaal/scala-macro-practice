import MacroHelper.{Doubler, MacroHelper}


object Macros {
  def main(args: Array[String]): Unit = {
    case class Foo(id: Int, name: String)
//    val foo: Foo = MacroHelper("""Foo(1, "mj")""")
//    val i: Int = MacroHelper("3 + 4 * 12")
//    val f: Unit = MacroHelper("val i = 15")
    implicit val doubledFoo = Doubler.double[Foo]
  }
}
