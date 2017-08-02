addSbtPlugin("com.waioeka.sbt" % "cucumber-plugin" % "0.1.4")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.2")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.11")

libraryDependencies ++= Seq(
    "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.1",
    "org.slf4j" % "slf4j-simple" % "1.7.25"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
