package MacroHelper

import java.io.File

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

object MacroHelper {
  import scala.reflect.runtime.{universe => u}

  def apply[A](string: String): A = {
    val toolbox = currentMirror.mkToolBox()
    val tree = toolbox.parse(string)
//    println(showRaw(tree))
    println(show(tree, 1))
    toolbox.eval(tree).asInstanceOf[A]
  }

  def show[T <: u.Tree](t: T, depth: Int): String = {
    val pad = "  " * depth
    t match {
      case u.ClassDef(mod, typeName, typeDefs, template) =>
        s"""ClassDef(
           |${pad}${mod},
           |${pad}TypeName(${typeName}),
           |${pad}${typeDefs},
           |${pad}${show(template, depth + 1)}
           |${pad})""".stripMargin
      case u.Template(parents, self, body) =>
        s"""Template(
           |${pad}${parents},
           |${pad}${show(self, depth + 1)},
           |${pad}${show(body, depth + 1)}
           |${pad})""".stripMargin
      case u.ValDef(mod, termName, tree1, tree2) =>
        s"""ValDef(
           |${pad}${mod},
           |${pad}${termName},
           |${pad}${show(tree1, depth + 1)},
           |${pad}${show(tree2, depth + 1)}
           |${pad})""".stripMargin
      case u.DefDef(mod, termName, typeDefs, valDefss, tree1, tree2) =>
        s"""DefDef(
           |${pad}${mod},
           |${pad}${termName},
           |${pad}${show(typeDefs, depth + 1)},
           |${pad}ValDefss(
           |${pad}  ${valDefss.map(valDefs => show(valDefs, depth + 1)).mkString(s"\n${pad}  ")}
           |${pad}  )
           |${pad}${show(tree1, depth + 1)},
           |${pad}${show(tree2, depth + 1)}
           |${pad})""".stripMargin
      case u.Apply(fun, args) =>
        s"""Apply(
           |${pad}${show(fun, depth + 1)},
           |${pad}${show(args, depth + 1)}
           |${pad})""".stripMargin
      case _ => showRaw(t)
    }
  }

  def show[T <: u.Tree](tlist: List[T], depth: Int): String = {
    val pad = "  " * depth
    s"""List(
       |${pad}${tlist.map(t => show(t, depth + 1)).mkString(s"\n${pad}")}
       |${pad})""".stripMargin
  }

  def showRaw(t: u.Tree): String = u showRaw t

  def fromFile[A](file: File): A =
    apply(scala.io.Source.fromFile(file).mkString(""))

  def fromFileName[A](file: String): A =
    fromFile(new File(file))

}

object Doubler {
  def double[T](t: T): T = macro doubleImpl

  def doubleImpl[T](c: blackbox.Context)(f: T): c.Expr[T] = {
    import c.universe._

    def doubleArgs(t: Tree): Tree = t match {
      case Literal(Constant(v)) =>
        val v2 = v match {
          case i: Int => i * 2
          case s: String => s * 2
          case _ => _
        }
        Literal(Constant(v2))
      case _ => _
    }

    f match {
      case Apply(fun, args) => Apply(fun, args.map(arg => double(arg)))
    }

    c.Expr(doubleArgs(f))
  }
}