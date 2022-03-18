package ru.skelantros.autoexchange.json_maker

import ru.skelantros.autoexchange.base.{AvroClass, Field, Relation}

object JsonMaker {
  private val primitiveTypes = Set("int", "double", "float", "string", "boolean")
  private val scalaKeywords = Set("type", "package")

  private def jsonType(field: Field): String = field.typ match {
    case t if primitiveTypes(t) => t.capitalize
    case "integer" => "Int"
    case t => t
  }

  private def completeType(field: Field): String = {
    val fieldType = jsonType(field)
    field.relation match {
      case Relation.Req => fieldType
      case Relation.Opt => s"Option[$fieldType]"
      case Relation.ReqArray => s"List[$fieldType]"
      case Relation.OptArray => s"Option[List[$fieldType]]"
    }
  }

  private def nameInCase(field: Field): String = field.name match {
    case n if scalaKeywords(n) => s"`$n`"
    case n => n
  }

  private def fieldInCase(field: Field, indentSize: Int): String =
    s"${" " * indentSize}${nameInCase(field)}: ${completeType(field)}"

  def apply(avro: AvroClass, packageName: String): String = {
    val indentSize = s"case class ${avro.name}(".length
    s"""package $packageName
       |
       |case class ${avro.name}(${fieldInCase(avro.fields.head, 0)}${if(avro.fields.size > 1) "," else ""}
       |${avro.fields.tail.map(fieldInCase(_, indentSize)).mkString(",\n")})""".stripMargin
  }
}
