package ru.skelantros.autoexchange.json_maker

import java.io.{File, FileWriter}

import ru.skelantros.autoexchange.base.{AvroClass, MarkupParser}
import ru.skelantros.autoexchange.markup_parser.ConfluenceAvroClassParser

object JsonMakerMain extends App {
  if(args.length < 3) {
    println("usage: SchemaBuilderMain (input directory) (output directory) (namespace)")
    System.exit(0)
  }

  val inputDir = new File(args(0))
  assert(inputDir.isDirectory, "Input dir should be a directory")
  val outputDir = new File(args(1))
  assert(outputDir.isDirectory, "Output dir should be a directory")
  val packageName = args(2)

  val markupFiles = inputDir.listFiles()
  val avroClasses = markupFiles.map(ConfluenceAvroClassParser(_))

  private def writeToFile(data: String, file: File): Unit = {
    val writer = new FileWriter(file)
    writer.write(data)
    writer.flush()
    writer.close()
  }

  for(avro <- avroClasses)
    writeToFile(JsonMaker(avro, packageName), new File(outputDir, avro.name + ".scala"))
}
