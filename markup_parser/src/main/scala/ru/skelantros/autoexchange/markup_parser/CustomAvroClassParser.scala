package ru.skelantros.autoexchange.markup_parser
import java.io.File

import ru.skelantros.autoexchange.base.{AvroClass, Field, Relation}

object CustomAvroClassParser extends AvroClassParser {
  private val fieldRegex =
    """^\"?[\s\xa0]*(\w+)[\s\xa0]*\t[\s\xa0]*(\w+)[\s\xa0]*\t[\s\xa0]*(01|11|0\*|1\*)[\s\xa0]*\t(\w*)\t([^\t]*?)\"?$""".r

  type Markup = Seq[Field]
  private def fromStrings(strs: Seq[String]): Markup =
    strs.map {
      case fieldRegex(name, typ, relation, default, doc) =>
        Field(name, typ, Relation(relation), doc, Option(default).filter(_.nonEmpty))
    }

  private val fileNameRegex = "(.+)\\.(.+)".r
  private val baseFileNameRegex = "_(.+)\\.(.+)".r

  def apply(file: File): AvroClass = {
    val (name, isRequired) = file.getName match {
      case baseFileNameRegex(name, _) => (name, true)
      case fileNameRegex(name, _) => (name, false)
    }

    val src = io.Source.fromFile(file)
    val fieldsStrs = try {
      src.getLines().toList
    } finally { src.close() }

    AvroClass(name, fromStrings(fieldsStrs), isRequired)
  }
}
