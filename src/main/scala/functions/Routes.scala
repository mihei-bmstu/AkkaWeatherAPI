package functions

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.duration._
import scala.concurrent.Future

class Routes(buildRepository: ActorRef[Repository.Command])(implicit system: ActorSystem[_]) extends JsonSupport {
  import akka.actor.typed.scaladsl.AskPattern.schedulerFromActorSystem
  import akka.actor.typed.scaladsl.AskPattern.Askable

  implicit val timeout: Timeout = 3.seconds

  lazy val theRoutes: Route =
    pathPrefix("jobs") {
      concat(
        pathEnd {
          concat(
            post {
              entity(as[Repository.Weather]) { job =>
                val operationPerformed: Future[Repository.Response] =
                  buildRepository.ask(Repository.AddJob(job, _))
                onSuccess(operationPerformed) {
                  case Repository.OK         => complete("Job added")
                  case Repository.KO(reason) => complete(StatusCodes.InternalServerError -> reason)
                }
              }
            },
            delete {
              val operationPerformed: Future[Repository.Response] =
                buildRepository.ask(Repository.ClearJobs(_))
              onSuccess(operationPerformed) {
                case Repository.OK         => complete("Jobs cleared")
                case Repository.KO(reason) => complete(StatusCodes.InternalServerError -> reason)
              }
            }
          )
        },
        (get & path(LongNumber)) { id =>
          val maybeJob: Future[Option[Repository.Weather]] =
            buildRepository.ask(Repository.GetCity("London", _))
          rejectEmptyResponse {
            complete(maybeJob)
          }
        }
      )
    }

}
