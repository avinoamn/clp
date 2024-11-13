package github.avinoamn.clp.utils

import io.circe.Decoder
import io.circe.generic.codec.DerivedAsObjectCodec

import scala.reflect.runtime.universe.{MethodSymbol, Type, runtimeMirror, termNames}
import scala.util.Try

private[clp] trait Reflection {
  implicit class TypeExtensions(tpe: Type) {
    def toClass: Class[_] = {
      runtimeMirror(getClass.getClassLoader).runtimeClass(tpe.typeSymbol.asClass)
    }

    def getCtor: MethodSymbol = {
      tpe.member(termNames.CONSTRUCTOR).asMethod
    }

    def getMembers: Map[String, Type] = tpe.members.collect {
      case m: MethodSymbol if m.isGetter => m.name.toString -> m.typeSignature.resultType
    }.toMap[String, Type]

    def isExtending(other: Type): Boolean = {
      tpe.baseClasses.contains(other.typeSymbol)
    }
  }

  implicit class ClassExtensions(cls: Class[_]) {
    def getCodec: Option[DerivedAsObjectCodec[_]] = {
      Option(
        Try(
          cls.getDeclaredMethod(s"codecFor${cls.getSimpleName}").invoke(this).asInstanceOf[DerivedAsObjectCodec[_]]
        ).getOrElse(null)
      )
    }

    def getDecoder: Option[Decoder[_]] = {
      Option(
        Try(
          cls.getDeclaredMethod(s"decode${cls.getSimpleName}").invoke(this).asInstanceOf[Decoder[_]]
        ).getOrElse(null)
      )
    }
  }
}
