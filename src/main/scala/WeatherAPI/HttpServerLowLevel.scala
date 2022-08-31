package WeatherAPI

import WeatherAPI.WeatherRequest.getTemp
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._

import scala.concurrent.{Await, CanAwait, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.util.{Failure, Success}

object HttpServerLowLevel {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "lowlevel")
    // needed for the future map/flatmap in the end
    implicit val executionContext: ExecutionContext = system.executionContext

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

    val bindingFuture = Http().newServerAt("localhost", 8080).bindSync(requestHandler)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
