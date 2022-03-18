package ru.skelantros.autoexchange.markup_parser

import java.io.File

import ru.skelantros.autoexchange.base.AvroClass

trait AvroClassParser {
  def apply(file: File): AvroClass
  def apply(path: String): AvroClass = this(new File(path))

  private val defaultValues = Map("snapshot" -> "2", "deleted_flag" -> "false", "system_id" -> "\"DC\"")
  protected def defaultValue(fieldName: String): Option[String] = defaultValues.get(fieldName)
}
