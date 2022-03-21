package ru.skelantros.autoexchange.markup_parser

import java.io.File

import ru.skelantros.autoexchange.base.{AvroClass, Field}

trait AvroClassParser {
  def apply(file: File): AvroClass
  def apply(path: String): AvroClass = this(new File(path))

  private val defaultValues = Map("snapshot" -> "2", "deleted_flag" -> "false", "system_id" -> "\"DC\"")
  private val ignoredFields = Set("date")

  private def isPartitionDate(field: Field): Boolean =
    field.name == "date" && field.doc == "Дата конвертации, используется для партиционирования"

  protected def isFieldPresent(field: Field): Boolean = !isPartitionDate(field)
  protected def defaultValue(fieldName: String): Option[String] = defaultValues.get(fieldName)
}
