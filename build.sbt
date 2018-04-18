import Dependencies._

scalaVersion := "2.12.5"
version := "0.1.0-SNAPSHOT"
name := "Scala Macro"

lazy val root = (project in file("."))
  .settings(sharedSettings)
  .dependsOn(helper)

lazy val helper = (project in file("macro-helper"))
  .settings(sharedSettings)
  .settings(mainClass := Some("Macros"))


lazy val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    scalaTest % Test,
    "org.scala-lang" % "scala-reflect" % "2.12.5",
    "org.scala-lang" % "scala-compiler" % "2.12.5"
  )
)
