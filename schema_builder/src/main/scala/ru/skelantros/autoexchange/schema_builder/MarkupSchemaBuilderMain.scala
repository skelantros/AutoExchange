package ru.skelantros.autoexchange.schema_builder

import java.io.{File, FileWriter}

import ru.skelantros.autoexchange.base.{AvroClass, MarkupParser}

// Строит AVRO-схемы из всех файлов в папке, построенных с помощью кастомной разметки
object MarkupSchemaBuilderMain extends App {
  if(args.length < 3) {
    println("usage: SchemaBuilderMain (input directory) (output directory) (namespace)")
    System.exit(0)
  }

  val inputDir = new File(args(0))
  assert(inputDir.isDirectory, "Input dir should be a directory")
  val outputDir = new File(args(1))
  assert(outputDir.isDirectory, "Output dir should be a directory")
  val namespace = args(2)

  val markupFiles = inputDir.listFiles()
  val avroClasses = markupFiles.map(MarkupParser.fromFile)

  private def writeToFile(data: String, file: File): Unit = {
    val writer = new FileWriter(file)
    writer.write(data)
    writer.flush()
    writer.close()
  }

  for(AvroClass(name, fields, _) <- avroClasses)
    writeToFile(SchemaBuilder(name, namespace, fields), new File(outputDir, name + ".avsc"))
}
