![Build Status](https://github.com/pjfanning/jackson-scala-reflect-extensions/actions/workflows/ci.yml/badge.svg?branch=main)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pjfanning/jackson-scala-reflect-extensions_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pjfanning/jackson-scala-reflect-extensions_3)

# jackson-scala-reflect-extensions

Jackson Scala 2 support that uses [scala-reflect](https://docs.scala-lang.org/overviews/reflection/overview.html)
to get type info.

This lib is designed to be used with [jackson-module-scala](https://github.com/FasterXML/jackson-module-scala). By default,
jackson-module-scala uses Java reflection to work out the class structure.

The Scala3 equivalent is [jackson-scala3-reflection-extensions](https://github.com/pjfanning/jackson-scala3-reflection-extensions).

`ScalaReflectExtensions` can be mixed into your ObjectMapper in as a similar way to jackson-module-scala's
[ClassTagExtensions](https://github.com/FasterXML/jackson-module-scala/blob/2.14/src/main/scala/com/fasterxml/jackson/module/scala/ClassTagExtensions.scala)
and [ScalaObjectMapper](https://github.com/FasterXML/jackson-module-scala/blob/2.14/src/main/scala-2.%2B/com/fasterxml/jackson/module/scala/ScalaObjectMapper.scala).

```scala
libraryDependencies += "com.github.pjfanning" %% "jackson-scala-reflect-extensions" % "2.13.3"
```

```scala
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.ScalaReflectExtensions

val mapperBuilder = JsonMapper.builder()
  .addModule(DefaultScalaModule)

val mapper = mapperBuilder.build() :: ScalaReflectExtensions

// this should also work but Jackson is moving to supporting only creating mapper instances from a builder
val mapper2 = new ObjectMapper with ScalaReflectExtensions
mapper2.registerModule(DefaultScalaModule)

val instance = mapper.readValue[MyClass](jsonText)
```

