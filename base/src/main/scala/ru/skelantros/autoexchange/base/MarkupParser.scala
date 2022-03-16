package ru.skelantros.autoexchange.base

object MarkupParser {
  private val fieldRegex =
    """(\w+)\t(\w+)\t(01|11|0\*|1\*)\t(\w*)\t([^\t]*)""".r

  type Markup = Seq[Field]
  def apply(strs: Seq[String]): Markup =
    strs.map {
      case fieldRegex(name, typ, relation, default, doc) =>
        Field(name, typ, Relation(relation), doc, Option(default).filter(_.nonEmpty))
    }
}
