package functions

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._


trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._
  import Repository._

  implicit object StatusFormat extends RootJsonFormat[Status] {
    def write(status: Status): JsValue = status match {
      case Failed     => JsString("Failed")
      case Successful => JsString("Successful")
    }

    def read(json: JsValue): Status = json match {
      case JsString("Failed")     => Failed
      case JsString("Successful") => Successful
      case _                      => throw new DeserializationException("Status unexpected")
    }
  }

  implicit val cityFormat: RootJsonFormat[Weather] = jsonFormat2(Weather)
}
