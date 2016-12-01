name := "scala-heroku"
version := "1.0"
scalaVersion := "2.12.0"

enablePlugins(JavaAppPackaging)

val akka = "2.4.14"
val akkaHttp = "10.0.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
  "com.typesafe.akka" %% "akka-http" % akkaHttp
)

mainClass in (Compile, run) := Some("Main")

// heroku config
herokuAppName in Compile := "ancient-badlands-57454"
herokuConfigVars in Compile := Map(
  "JAVA_OPTS" -> "-Xmx1024m -Xss512k -XX:+UseCompressedOops"
)
herokuProcessTypes in Compile := Map(
  "web" -> "target/universal/stage/bin/scala-heroku -Dhttp.port=$PORT"
)
