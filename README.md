# clp
A flexible and easy-to-use command-line parser for Scala programs.

This project provides a powerful way to parse command-line arguments into Scala case classes, supporting various data types such as `String`, `Int`, `List[_]`, and even JSON-encoded strings.

## Installation

To include this library in your project, clone this repository and install it locally.

For Maven users, add the following dependency to your `pom.xml`:

```xml
<dependency>
   <groupId>github.avinoamn</groupId>
   <artifactId>clp_${scala.major.version}</artifactId>
   <version>1.0.0</version>
   <scope>compile</scope>
</dependency>
```

## Basic program args example
```
case class Args(a: String, b: Int, c: List[String])

val args: Array = Array("--a", "asd", "--b", "123", "--c", "a,s,d")

CommandLineParser.parse[Args](args)
```

## Case class program arg example (from json string)
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

## Supported Types:
* `String`
* `Boolean`
* `Int`
* `Long`
* `Float`
* `Double`
* `List[_]`: `_` can be of any supported type
* `case class`: must have the `@JsonCodec` annotation and extend either `JsonString` or `Base64EncodedJsonString` traits for the decoding to succeed
