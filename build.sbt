name := "apache-avro-test"

organization := "com.github.dnvriend"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.3"

scalacOptions += "-Ypartial-unification"
scalacOptions += "-Ydelambdafy:inline"
scalacOptions += "-unchecked"
scalacOptions += "-deprecation"
scalacOptions += "-language:higherKinds"
scalacOptions += "-language:implicitConversions"
scalacOptions += "-feature"
//scalacOptions += "-Xfatal-warnings"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.16"
libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.11.0"
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.8.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.6"
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.2" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % Test

// testing configuration
fork in Test := true
parallelExecution := false

// enable scala code formatting //
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

// Scalariform settings
SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
   .setPreference(AlignSingleLineCaseStatements, true)
   .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
   .setPreference(DoubleIndentConstructorArguments, true)
   .setPreference(DanglingCloseParenthesis, Preserve)

// enable updating file headers //
organizationName := "Dennis Vriend"
startYear := Some(2017)
licenses := Seq(("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")))
headerMappings := headerMappings.value + (HeaderFileType.scala -> HeaderCommentStyle.CppStyleLineComment)

// enable code rewrite and linting //
scalacOptions ++= scalafixScalacOptions.value
scalafixVerbose := false

val lintAndRewrite = taskKey[Unit]("Lints and rewrites Scala code using defined rules")

lintAndRewrite := {
  // see: https://scalacenter.github.io/scalafix/docs/users/rules
  List(
    "RemoveUnusedImports", // https://scalacenter.github.io/scalafix/docs/rules/RemoveUnusedImports
    "ExplicitResultTypes", // https://scalacenter.github.io/scalafix/docs/rules/ExplicitResultTypes
    "ProcedureSyntax", // https://scalacenter.github.io/scalafix/docs/rules/ProcedureSyntax
    "DottyVolatileLazyVal", // https://scalacenter.github.io/scalafix/docs/rules/DottyVolatileLazyVal
    "ExplicitUnit", // https://scalacenter.github.io/scalafix/docs/rules/ExplicitUnit
    "DottyVarArgPattern", // https://scalacenter.github.io/scalafix/docs/rules/DottyVarArgPattern
    "NoAutoTupling", // https://scalacenter.github.io/scalafix/docs/rules/NoAutoTupling
    "NoValInForComprehension", // https://scalacenter.github.io/scalafix/docs/rules/NoValInForComprehension
    "NoInfer", // https://scalacenter.github.io/scalafix/docs/rules/NoInfer
  ).map(rule => s" $rule")
    .map(rule => scalafix.toTask(rule))
    .reduce(_ dependsOn _).value
}

enablePlugins(AutomateHeaderPlugin, SbtScalariform)
