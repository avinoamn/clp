package github.avinoamn.clp.types

import io.circe.Json

import java.util.Base64

trait Base64EncodedJsonString

object Base64EncodedJsonString {
  def decode(value: String): Json = JsonString.parse(new String(Base64.getDecoder.decode(value), "UTF-8"))
}
