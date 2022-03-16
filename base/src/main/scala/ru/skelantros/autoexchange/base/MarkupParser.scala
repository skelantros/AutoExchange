package ru.skelantros.autoexchange.base

import java.io.{File, FileReader}

object MarkupParser {
  private val fieldRegex =
    """(\w+)\t(\w+)\t(01|11|0\*|1\*)\t(\w*)\t([^\t]*)""".r

  type Markup = Seq[Field]
  def apply(strs: Seq[String]): Markup =
    strs.map {
      case fieldRegex(name, typ, relation, default, doc) =>
        Field(name, typ, Relation(relation), doc, Option(default).filter(_.nonEmpty))
    }

  private val fileNameRegex = "(.+)\\.(.+)".r
  def fromFile(file: File): AvroClass = {
    val fileNameRegex(name, _) = file.getName
    val isRequired = name.startsWith("_")

    val src = io.Source.fromFile(file)
    val fieldsStrs = try {
      src.getLines().toSeq
    } finally { src.close() }

    AvroClass(name, MarkupParser(fieldsStrs), isRequired)
  }

  def fromFile(path: String): AvroClass = fromFile(new File(path))
}
