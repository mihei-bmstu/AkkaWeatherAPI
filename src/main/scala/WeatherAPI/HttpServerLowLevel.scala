package WeatherAPI

import WeatherAPI.WeatherRequest.getTemp
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object HttpServerLowLevel {

  def getHttpResponse(location: String): HttpResponse = {
    val forecast = getTemp(location)
    val temp = Await.result(forecast, 5.seconds)
    HttpResponse(entity = HttpEntity(
      ContentTypes.`text/html(UTF-8)`,
      s"Current temperature in the location '$location' is $temp °C"
      )
    )
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "lowlevel")
    implicit val executionContext: ExecutionContext = system.executionContext

    system.settings

    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
        HttpResponse(entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          "Please send me City and Country. For example: Moscow,rus"))

      case HttpRequest(GET, Uri.Path(location), _, _, _) =>
          getHttpResponse(location.substring(1))

      case r: HttpRequest =>
        r.discardEntityBytes() // important to drain incoming HTTP Entity stream
        HttpResponse(404, entity = "Please send me City and Country. For example: Moscow,rus")
    }

    val port = 8080
    val host = "localhost"
    val bindingFuture: Future[Http.ServerBinding] =
      Http().newServerAt(host, port).bindSync(requestHandler)

    println(s"Server online at $host:$port ")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
