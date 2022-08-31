package WeatherAPI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import com.example.Weather
import upickle.default

import scala.concurrent.{CanAwait, Future}
import scala.concurrent.duration.DurationInt

object WeatherRequest {

  implicit val system: ActorSystem = ActorSystem()
  //implicit val materializer: Materializer = Materializer

  import system.dispatcher

  case class ErrorStatus(code: String, description: String)
  case class ResponseStatus(success: Boolean, error: ErrorStatus)
  case class Temperature(tempC: Float)

  def sendRequest(request: HttpRequest): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
    entityFuture.map(entity => entity.data.utf8String)
  }

  def parseResponse(response: String): String = {
    implicit val tempRW: default.ReadWriter[Temperature] = upickle.default.macroRW[Temperature]
    implicit val errorRW: default.ReadWriter[ErrorStatus]  = upickle.default.macroRW[ErrorStatus]
    implicit val responseStatusRW: default.ReadWriter[ResponseStatus]  = upickle.default.macroRW[ResponseStatus]
    val jsonResponse = ujson.read(response)
    val ifSuccess = upickle.default.read[ResponseStatus](jsonResponse)
    if (ifSuccess.success) {
      val temperature = upickle.default.read[Temperature](jsonResponse("response")("ob"))
      temperature.tempC.toString
    }  else ifSuccess.error.description

  }

  def getTemp(cityCountry: String = "London,gb"): Future[String] = {
    val request: HttpRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = "https://aerisweather1.p.rapidapi.com/observations/" + cityCountry
    )
      .withHeaders(
        RawHeader("X-RapidAPI-Key", "f452e61143msh123aa1934dc90a6p12848bjsn8b09796dd8a2"),
        RawHeader("X-RapidAPI-Host", "aerisweather1.p.rapidapi.com")
      )

    val response = sendRequest(request)
    response.foreach(println)
    val temp = response.map(parseResponse)
    temp
  }
  /*
  def main(args: Array[String]): Unit = {
    val temp = getTemp("Moscow,rus")
    temp.onComplete {
      case Success(value) => println(value)
    }
  }*/

}
