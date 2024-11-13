package github.avinoamn.clp.utils

import github.avinoamn.clp.consts.Consts.CommandLineArgs.listItemSeparator
import github.avinoamn.clp.types.{Base64EncodedJsonString, JsonString}
import io.circe.{Decoder, Json}
import io.circe.generic.codec.DerivedAsObjectCodec

import scala.reflect.runtime.universe.{Type, typeOf}

private[clp] object ValuesHandler extends Reflection {
  private def handleCCValue(tpe: Type, value: String): Product = {
    val cls = tpe.toClass

    val json: Json = tpe match {
      case tpe if tpe.isExtending(typeOf[JsonString]) => JsonString.parse(value)
      case tpe if tpe.isExtending(typeOf[Base64EncodedJsonString]) => Base64EncodedJsonString.decode(value)
      case _ => throw new Exception(s"Program arg Case Class ${cls.getSimpleName} must extend `JsonString` or `Base64EncodedJsonString`")
    }

    ((cls.getCodec, cls.getDecoder) match {
      case (Some(codec: DerivedAsObjectCodec[_]), _) => codec.decodeJson(json)
      case (_, Some(codec: Decoder[_])) => codec.decodeJson(json)
      case _ => throw new Exception(s"No Encoder / Codec is defined for case class ${cls.getSimpleName} (use the io.circe.generic.JsonCodec annotation).")
    }) match {
      case Right(cc) => cc.asInstanceOf[Product]
      case Left(err) => throw new Exception(err.message)
    }
  }

  private def handleListValue(tpe: Type, value: String): List[_] = {
    value.split(listItemSeparator).map(handleValue(tpe.typeArgs.head, _)).toList
  }

  def handleValue(fieldType: Type, value: String): Any = fieldType match {
    case ft if ft =:= typeOf[String] => value
    case ft if ft =:= typeOf[Boolean] => value.toBoolean
    case ft if ft =:= typeOf[Int] => value.toInt
    case ft if ft =:= typeOf[Long] => value.toLong
    case ft if ft =:= typeOf[Float] => value.toFloat
    case ft if ft =:= typeOf[Double] => value.toDouble
    case ft if ft.typeSymbol == typeOf[List[_]].typeSymbol => handleListValue(ft, value)
    case ft if ft.typeSymbol.asClass.isCaseClass => handleCCValue(ft, value)
    case ft => throw new Exception(s"Field type `${ft.toClass.getSimpleName}` is not supported")
  }
}
