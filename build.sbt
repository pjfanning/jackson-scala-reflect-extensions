import sbtghactions.JavaSpec.Distribution.Zulu

name := "jackson-scala-reflect-extensions"
organization := "com.github.pjfanning"
description := "Extension to jackson-module-scala that uses scala-reflect to get type info"

ThisBuild / scalaVersion := "2.13.16"

ThisBuild / crossScalaVersions := Seq("2.11.12", "2.12.20", "2.13.16")

val jacksonVersion = "2.18.3"

//resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "org.slf4j" % "slf4j-api" % "2.0.17",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.slf4j" % "slf4j-simple" % "2.0.17" % Test,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % jacksonVersion % Test
)

// build.properties
Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "com" / "github" / "pjfanning" / "jackson" / "reflect" / "build.properties"
  val contents = "version=%s\ngroupId=%s\nartifactId=%s\n".format(version.value, organization.value, name.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue

homepage := Some(url("https://github.com/pjfanning/jackson-scala-reflect-extensions"))

licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

developers := List(
  Developer(id="pjfanning", name="PJ Fanning", email="", url=url("https://github.com/pjfanning"))
)

Test / parallelExecution := false

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "8"))
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test")))
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
    )
  )
)
