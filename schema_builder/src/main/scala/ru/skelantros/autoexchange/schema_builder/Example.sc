import ru.skelantros.autoexchange._
import base._
import schema_builder._

val input = s"""order_event${"\t"}OrderEvent${"\t"}11${"\t"}${"\t"}Событие над заказом
              |order_type_cdb_id${"\t"}int${"\t"}01${"\t"}312345${"\t"}Код типа заказа используемый для интеграций
              |service_cdb_id${"\t"}int${"\t"}0*${"\t"}${"\t"}Код услуги используемый для интеграций""".stripMargin

val fields = MarkupParser(input.split("\n"))

val res = SchemaBuilder("name", "namesapce", fields)