
import sbt._
import Keys._

object Settings {
  lazy val basicSettings = Seq[Setting[_]](
    scalaVersion := "2.11.8"
    , scalacOptions := Seq("-deprecation", "-encoding", "utf8")
    , organization := ""
    , version := "0.1.0"
    , description := "new project"
    , resolvers ++=
      Seq(
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        "bintray/non" at "http://dl.bintray.com/non/maven",
        "bintray/scalaz" at "http://dl.bintray.com/scalaz/releases"
      )
  )
}

object deps {
  object v {
    val scalaz        = "7.2.1"
    val nscalaTime    = "1.2.0"
    val jodaTime      = "2.3"
    val macroParadise = "2.1.0"
  }
  val scalaz = Seq(
    "org.scalaz" %% "scalaz-core" % v.scalaz,
    "org.scalaz" %% "scalaz-concurrent" % v.scalaz
  )

  val time = Seq(
    "joda-time" % "joda-time" % v.jodaTime,
    "com.github.nscala-time" %% "nscala-time" % v.nscalaTime
  )

}

object ProjectBuild extends Build {

  import Settings._

  lazy val macros =
    Project("macros", file("macros"))
      .settings(basicSettings: _*)
      .settings(
        projectDependencies ++=
          deps.scalaz ++
            deps.time
      )
      .settings(
        libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),
        libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _),
        libraryDependencies := {
          CrossVersion.partialVersion(scalaVersion.value) match {
            // if Scala 2.11+ is used, quasiquotes are available in the standard distribution
            case Some((2, x)) if x >= 11 =>
              libraryDependencies.value
            // in Scala 2.10, quasiquotes are provided by macro paradise
            case Some((2, 10)) =>
              libraryDependencies.value ++ Seq(
                compilerPlugin("org.scalamacros" % "paradise" % deps.v.macroParadise cross CrossVersion.full),
                "org.scalamacros" %% "quasiquotes" % deps.v.macroParadise cross CrossVersion.binary)
          }
        }
      )

  lazy val msc =
    Project("macro-smart-constructors", file("."))
      .settings(basicSettings: _*)
      .dependsOn(macros)
}
