package functions

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import models.CityWeather
import functions.FetchRecord

object Repository {

  sealed trait Status
  object Successful extends Status
  object Failed extends Status

  final case class Weather(city: String, temperature: Float)

  sealed trait Response
  case object OK extends Response
  final case class KO(reason: String) extends Response

  sealed trait Command
  final case class GetCity(city: String, replyTo: ActorRef[Option[Weather]]) extends Command
  final case class AddJob(job: Weather, replyTo: ActorRef[Response]) extends Command
  final case class ClearJobs(replyTo: ActorRef[Response]) extends Command

  def apply(city: String = "London"): Behavior[Command] = Behaviors.receiveMessage {
    case GetCity(city, replyTo) =>
      replyTo ! Option(FetchRecord.findCity(city).last.asInstanceOf[Weather])
      Behaviors.same
    case AddJob(job, replyTo) =>
      Behaviors.same
    case ClearJobs(replyTo) =>
      Behaviors.same
  }

}
