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
  private val baseFileNameRegex = "_(.+)\\.(.+)".r
  def fromFile(file: File): AvroClass = {
    val (name, isRequired) = file.getName match {
      case baseFileNameRegex(name, _) => (name, true)
      case fileNameRegex(name, _) => (name, false)
    }

    val src = io.Source.fromFile(file)
    val fieldsStrs = try {
      src.getLines().toSeq
    } finally { src.close() }

    AvroClass(name, MarkupParser(fieldsStrs), isRequired)
  }

  def fromFile(path: String): AvroClass = fromFile(new File(path))
}
