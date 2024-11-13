package github.avinoamn.clp

import github.avinoamn.clp.consts.Consts.CommandLineArgs.argumentPrefix
import github.avinoamn.clp.utils.Reflection
import github.avinoamn.clp.utils.ValuesHandler.handleValue

import scala.annotation.tailrec
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.{Type, TypeTag, typeOf}

object CommandLineParser extends Reflection {
  @tailrec
  private def parseArgs(args: List[String], map: Map[String, Any] = Map())(implicit members: Map[String, Type]): Map[String, Any] = {
    if (args.isEmpty) return map

    val arg :: value :: tail = args

    if (arg.startsWith(argumentPrefix)) {
      val fieldName = arg.substring(argumentPrefix.length)
      val fieldType = members(fieldName)

      val fieldValue = handleValue(fieldType, value)

      parseArgs(tail, map + (fieldName -> fieldValue))
    } else {
      parseArgs(args.tail, map)
    }
  }

  private def parseArgs(tpe: Type, args: List[String]): List[Any] = {
    implicit val members: Map[String, Type] = tpe.getMembers
    val parsedArgs = parseArgs(args)

    parsedArgs.values.toList
  }

  def parse[T: TypeTag](args: Array[String]): T = {
    val tpe = typeOf[T]

    val ctor = tpe.getCtor
    val classSymbol = tpe.typeSymbol.asClass

    // Reflectively get the class mirror and constructor mirror
    val classMirror = currentMirror.reflectClass(classSymbol)
    val constructorMirror = classMirror.reflectConstructor(ctor)

    // Invoke the constructor with the provided arguments
    constructorMirror(parseArgs(tpe, args.toList): _*).asInstanceOf[T]
  }
}
