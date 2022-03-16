package ru.skelantros.autoexchange.base

sealed trait Relation
object Relation {
  case object Opt extends Relation
  case object Req extends Relation
  case object OptArray extends Relation
  case object ReqArray extends Relation
  def apply(str: String): Relation = str match {
    case "11" => Req
    case "01" => Opt
    case "0*" => OptArray
    case "1*" => ReqArray
  }
}