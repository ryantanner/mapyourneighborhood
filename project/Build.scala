import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mapyourhood"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.typesafe" % "slick_2.10.0-M7" % "0.11.1",
      "org.webjars" % "bootstrap" % "2.1.1",
      "org.webjars" % "backbonejs" % "0.9.2"
    )

    val dataImportSubProject = Project(
      "data-import",
      file("modules/data-import")
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    )

}
