package ru.skelantros.autoexchange.markup_parser
import java.io.{ByteArrayInputStream, File}

import com.univocity.parsers.tsv.{TsvParser, TsvParserSettings}
import ru.skelantros.autoexchange.base.{AvroClass, Field, Relation}

import scala.collection.JavaConverters._

object ConfluenceAvroClassParser extends AvroClassParser {
  // инициализация парсера
  private val settings = new TsvParserSettings()
  settings.getFormat.setLineSeparator("\n")
  private val parser = new TsvParser(settings)

  type TsvRow = Array[String]

  // Эта замечательная функция считывает данные из файла и избавляется от лишних символов (в частности переносов строки посередине записи)
  private def prepareConfluence(file: File): String = {
    val src = io.Source.fromFile(file, "UTF-16")
    val res = try {
      src.mkString
    } finally { src.close() }

    res.replace("\"\n", "__REALNEWLINE__")
      .replace("\t\n", "__TABNEWLINE__")
      .replace("\n", "\\n")
      .replace("__REALNEWLINE__", "\"\n")
      .replace("__TABNEWLINE__", "\t\n")
  }

  private val strRegex = """"(.*)"""".r
  // Эта функция "обрабатывает" строку из TSV: заменяет символ новой строки на текст и экранирует кавычку (для записи в AVRO)
  def strFromTsv(str: String): String = {
    val strRegex(res) = str.replace("\n", "\\n")
    res.replace("\"", "\\\"")
  }

  // поиск номеров столбцов, которые задают информацию о названии поля, его типе, кратности и описании
  private def nameIdx(header: TsvRow): Int = header.indexWhere(_.toLowerCase.contains("поле avro"))
  private def typeIdx(header: TsvRow): Int = header.indexWhere(_.toLowerCase.contains("тип"))
  private def relationIdx(header: TsvRow): Int = header.indexWhere(_.toLowerCase.contains("кратность"))
  private def descIdx(header: TsvRow): Int = header.indexWhere(_.toLowerCase.contains("Описание атрибута (из BRD)".toLowerCase))
  private def jsonIdx(header: TsvRow): Int = header.indexWhere(_.toLowerCase.contains("json"))

  // функции, достающие данные из TSV
  private def nameOf(row: TsvRow, config: HeaderConfig): String =
    strFromTsv(row(config.nameIdx))
  private def typeOf(row: TsvRow, config: HeaderConfig): String =
    strFromTsv(row(config.typeIdx))
  private def relationOf(row: TsvRow, config:HeaderConfig): Relation = strFromTsv(row(config.relationIdx)).trim match {
    case "0..1" => Relation.Opt
    case "1..1" => Relation.Req
    case "0..*" => Relation.OptArray
    case "1..*" => Relation.ReqArray
    case _ => throw new Exception(s"Wrong relation type in file ${config.fileName}, field name: ${nameOf(row, config)}")
  }
  private def descOf(row: TsvRow, config: HeaderConfig): String = strFromTsv(row(config.descIdx))
  private def jsonOf(row: TsvRow, config: HeaderConfig): Option[String] =
    Option(row(config.jsonIdx)).map(strFromTsv).filter(_.nonEmpty)

  // вспомогательный кейс-класс, хранящий информацию о таблице
  private case class HeaderConfig(nameIdx: Int, typeIdx: Int, relationIdx: Int, descIdx: Int, fileName: String, jsonIdx: Int)
  private object HeaderConfig {
    def apply(fileName: String, header: TsvRow): HeaderConfig = HeaderConfig(
      nameIdx(header),
      typeIdx(header),
      relationIdx(header),
      descIdx(header),
      fileName,
      jsonIdx(header)
    )
  }
  private val fileNameRegex = "(.+)\\.(.+)".r
  private val baseFileNameRegex = "_(.+)\\.(.+)".r

  // собственно парсинг в AvroClass
  override def apply(file: File): AvroClass = {
    println(file.getName)

    val (name, isRequired) = file.getName match {
      case baseFileNameRegex(name, _) => (name, true)
      case fileNameRegex(name, _) => (name, false)
    }

    val prepared = prepareConfluence(file)
    val stream = new ByteArrayInputStream(prepared.getBytes("UTF-16"))

    val tsvParsed = parser.parseAll(stream, "UTF-16").asScala
    val headerConfig = HeaderConfig(file.getName, tsvParsed.head)

    val fields = tsvParsed.tail.map { row =>
      Field(nameOf(row, headerConfig), typeOf(row, headerConfig), relationOf(row, headerConfig), descOf(row, headerConfig), None, jsonOf(row, headerConfig))
    }

    AvroClass(name, fields.toVector, isRequired)
  }
}
