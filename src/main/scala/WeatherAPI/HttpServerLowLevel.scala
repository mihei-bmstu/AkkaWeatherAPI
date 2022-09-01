package WeatherAPI

import WeatherAPI.WeatherRequest.getTemp
import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object HttpServerLowLevel {

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
        try {
          val forecast = getTemp(location.substring(1))
          HttpResponse(entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            Await.result(forecast, 5.seconds)
          ))
        }

      case r: HttpRequest =>
        r.discardEntityBytes() // important to drain incoming HTTP Entity stream
        HttpResponse(404, entity = "Please send me City and Country. For example: Moscow,rus")
    }

    val bindingFuture: Future[Http.ServerBinding] =
      Http().newServerAt("localhost", 8081).bindSync(requestHandler)

    println(s"Server online at http://localhost:8081")

/*    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())*/
  }

}
