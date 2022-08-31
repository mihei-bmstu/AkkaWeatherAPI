package com.example

//#json-formats
import com.example.Registry.ActionPerformed
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val cityJsonFormat: RootJsonFormat[City] = jsonFormat1(City)
  implicit val citiesJsonFormat: RootJsonFormat[Cities] = jsonFormat1(Cities)

  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed)
}
//#json-formats
