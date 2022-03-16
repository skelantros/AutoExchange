package ru.skelantros.autoexchange.base

case class Field(name: String, typ: Field.Type, relation: Relation, doc: String, default: Option[String]) {
  import Relation._

  def inSchema: String =
    s""""name": "$name", "type": $typeInSchema,${default.map(v => s"\"default\": $v, ").getOrElse(" ")} "doc": "$doc" """

  lazy val typeInSchema: String = relation match {
    case OptArray =>
      s"[\"null\", {\"type\": \"array\", \"items\": \"$typ\", \"default\": []}]"
    case ReqArray =>
      s"[{\"type\": \"array\", \"items\": \"$typ\", \"default\": []}]"
    case Opt =>
      s"[\"null\", \"$typ\"]"
    case Req => s"\"$typ\""
  }
}

object Field {
  type Type = String
}