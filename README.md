# clp
Command-Line-Parser for Scala programs

## Basic program args example
```
case class Args(a: String, b: Int, c: List[String])

val args: Array = Array("--a", "asd", "--b", "123", "--c", "a,s,d")

CommandLineParser.parse[Args](args)
```

## Case class program arg example (from json string)
*`case class` args must have the `@JsonCodec` annotation or decoding them will fail.
```
import github.avinoamn.clp.types.JsonString
import io.circe.generic.JsonCodec

@JsonCodec case class Arg(a: String) extends JsonString
case class Args(a: Arg)

val args: Array = Array("--a", "{\"a\": \"asd\"}")

CommandLineParser.parse[Args](args)
```

## Case class program arg example (from base64 encoded json string)
```
import github.avinoamn.clp.types.Base64EncodedJsonString
import io.circe.generic.JsonCodec

@JsonCodec case class Arg(a: String) extends Base64EncodedJsonString
case class Args(a: Arg)

val args: Array = Array("--a", "eyJhIjogImFzZCJ9")

CommandLineParser.parse[Args](args)
```