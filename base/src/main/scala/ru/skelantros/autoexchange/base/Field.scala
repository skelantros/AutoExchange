package ru.skelantros.autoexchange.base

case class Field(name: String, typ: Field.Type, relation: Relation, doc: String, default: Option[String]) {
  import Relation._

  lazy val typeInSchema: String = relation match {
    case OptArray =>
      s"""[\"null\", {\"type\": \"array\", \"items\": \"$typ\", \"default\": []}]"""
    case ReqArray =>
      s"""[{\"type\": \"array\", \"items\": \"$typ\", \"default\": []}]"""
    case Opt =>
      s"""[\"null\", \"$typ\"]"""
    case Req => s"""\"$typ\""""
  }

  lazy val defaultInSchema: Option[String] = default match {
    case Some(x) => Some(x)
    case None if relation.isOptional => Some("null")
    case None => None
  }
}

object Field {
  type Type = String
}