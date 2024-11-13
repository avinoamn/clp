package github.avinoamn.clp.types

import io.circe.{Json, ParsingFailure}

trait JsonString

object JsonString {
  def parse(value: String): Json = {
    val either = io.circe.parser.parse(value)
    either match {
      case Right(json: Json) => json
      case Left(err: ParsingFailure) => throw new Exception(err.message)
    }
  }
}

