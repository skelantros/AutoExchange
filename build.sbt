name := "AutoExchange"

version := "0.1"

scalaVersion := "2.13.8"

lazy val baseProject = (project in file("base"))
  .settings(
    name := "Base"
  )

lazy val schemaBuilder = (project in file("schema_builder"))
  .dependsOn(baseProject, markupParser)
  .settings(
    name := "Schema Builder"
  )

lazy val importMaker = (project in file("import_maker"))
  .dependsOn(baseProject)
  .settings(
    name := "Import Maker"
  )

lazy val flumeMaker = (project in file("flume_maker"))
  .dependsOn(baseProject)
  .settings(
    name := "Flume Builder"
  )

lazy val sandbox = (project in file("sandbox"))
  .dependsOn(baseProject, schemaBuilder, importMaker, flumeMaker)

lazy val markupParser = (project in file("markup_parser"))
  .dependsOn(baseProject)
  .settings(
    name := "Avro Class Parser",
    libraryDependencies += "com.univocity" % "univocity-parsers" % "2.9.1"
  )