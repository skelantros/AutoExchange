package ru.skelantros.autoexchange.json_maker

import ru.skelantros.autoexchange.base.{AvroClass, Field, Relation}

object JsonMaker {
  private val primitiveTypes = Set("int", "double", "float", "string", "boolean")
  private val scalaKeywords = Set("type", "package")
  private val keyValues = Set("system.version", "system.name", "msgId", "creationTime")

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

  private def nameInCase(field: Field): Option[String] = field.json flatMap {
    case n if keyValues(n) => None
    case n if scalaKeywords(n) => Some(s"`$n`")
    case n => Some(n)
  }

  private def fieldInCase(field: Field, indentSize: Int): Option[String] = nameInCase(field) map {
    n => s"${" " * indentSize}${n}: ${completeType(field)}"
  }

  def apply(avro: AvroClass, packageName: String): String = {
    val indentSize = s"case class ${avro.name}(".length

    val fieldsInCase = avro.fields.map(fieldInCase(_, indentSize)) collect {
      case Some(f) => f
    }

    s"""package $packageName
       |
       |case class ${avro.name}(${fieldsInCase.head.trim}${if(fieldsInCase.size > 1) "," else ""}
       |${fieldsInCase.tail.mkString(",\n")})""".stripMargin
  }
}
