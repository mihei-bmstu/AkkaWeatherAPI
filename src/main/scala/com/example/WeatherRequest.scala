package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import akka.stream.Materializer
import upickle.default

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}

object WeatherRequest {

  implicit val system: ActorSystem = ActorSystem()
  //implicit val materializer: Materializer = Materializer
  import system.dispatcher
  case class Temperature(tempC: Float)

  def sendRequest(request: HttpRequest): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
    entityFuture.map(entity => entity.data.utf8String)
  }

  def parseResponse(response: String): Float = {
    implicit val tempRW: default.ReadWriter[Temperature] = upickle.default.macroRW[Temperature]
    val jsonResponse = ujson.read(response)
    val temperature = upickle.default.read[Temperature](jsonResponse("response")("ob"))
    temperature.tempC
  }

  def getTemp(cityCountry: String = "London,gb"): Future[Float] = {
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
    response.map(parseResponse)
  }

  def main(args: Array[String]): Unit = {
    val temp = getTemp("Moscow,rus")
    temp.onComplete {
      case Success(value) => println(value)
    }
  }

}
