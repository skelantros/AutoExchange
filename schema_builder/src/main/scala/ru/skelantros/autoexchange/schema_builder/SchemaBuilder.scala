package ru.skelantros.autoexchange.schema_builder

import ru.skelantros.autoexchange.base.Field

object SchemaBuilder {
  private def padString(str: String, reqLength: Int): String =
    str + (" " * (reqLength - str.length))

  private def fieldNameInSchema(field: Field, reqLength: Int): String =
    padString(s""""name" : "${field.name}", """, reqLength)

  private def fieldTypeInSchema(field: Field, reqLength: Int): String =
    padString(s""""type" : ${field.typeInSchema}, """, reqLength)

  private def fieldDefaultInSchema(field: Field, reqLength: Int): String = {
    val str = field.defaultInSchema.fold("")(defVal => s""""default" : $defVal, """)
    padString(str, reqLength)
  }

  private def docInSchema(field: Field): String =
    s""""doc" : "${field.doc}""""

  private def fieldInSchema(field: Field, nameLength: Int, typeLength: Int, defaultLength: Int): String =
    s"""${fieldNameInSchema(field, nameLength)}${fieldTypeInSchema(field, typeLength)}${fieldDefaultInSchema(field, defaultLength)}${docInSchema(field)}"""

  def apply(name: String, namespace: String, fields: Seq[Field]): String = {
    val fieldNameLength = s""""name" : "${fields.map(_.name).maxBy(_.length)}", """.length
    val fieldTypeLength = s""""type" : ${fields.map(_.typeInSchema).maxBy(_.length)}, """.length
    val defaultValues = fields.map(_.defaultInSchema).collect {
      case Some(x) => x
    }
    val fieldDefaultLength =
      if(defaultValues.isEmpty) 0
      else s""""default": "${defaultValues.maxBy(_.length)}", """.length

    s"""{
       |${" " * 4}"type" : "record",
       |${" " * 4}"name" : "$name",
       |${" " * 4}"namespace" : "ru.russianpost.dc.$namespace",
       |
       |${" " * 4}"fields" : [
       |${fields.map(f => s"${" " * 8}{${fieldInSchema(f, fieldNameLength, fieldTypeLength, fieldDefaultLength)}}").mkString(",\n")}
       |${" " * 4}]
       |}""".stripMargin
  }
}
