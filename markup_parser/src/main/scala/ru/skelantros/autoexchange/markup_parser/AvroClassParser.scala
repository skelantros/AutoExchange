package ru.skelantros.autoexchange.markup_parser

import java.io.File

import ru.skelantros.autoexchange.base.AvroClass

trait AvroClassParser {
  def apply(file: File): AvroClass
  def apply(path: String): AvroClass = this(new File(path))
}
