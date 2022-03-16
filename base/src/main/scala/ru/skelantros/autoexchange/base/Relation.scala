package ru.skelantros.autoexchange.base

sealed abstract class Relation(val isRequired: Boolean, val isArray: Boolean) {
  def isOptional: Boolean = !isRequired
}
object Relation {
  case object Opt extends Relation(false, false)
  case object Req extends Relation(true, false)
  case object OptArray extends Relation(false, true)
  case object ReqArray extends Relation(true, true)
  def apply(str: String): Relation = str match {
    case "11" => Req
    case "01" => Opt
    case "0*" => OptArray
    case "1*" => ReqArray
  }
}