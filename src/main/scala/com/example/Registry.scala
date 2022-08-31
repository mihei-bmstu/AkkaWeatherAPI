package com.example

//#user-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import WeatherAPI.WeatherRequest.getTemp

import scala.concurrent.Future

//#user-case-classes
final case class City(name: String)
final case class Cities(users: immutable.Seq[City])
final case class Weather(city: String, temperature: Future[Float])
//#user-case-classes

object Registry {
  // actor protocol
  sealed trait Command
  final case class GetCities(replyTo: ActorRef[Cities]) extends Command
  final case class InsertCity(city: City, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetCity(name: String, replyTo: ActorRef[GetCityResponse]) extends Command
  final case class DeleteCity(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetCityResponse(maybeCity: Option[City])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(cities: Set[City]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetCities(replyTo) =>
        replyTo ! Cities(cities.toSeq)
        Behaviors.same
      case InsertCity(city, replyTo) =>
        replyTo ! ActionPerformed(s"City ${city.name} inserted.")
        registry(cities + city)
      case GetCity(name, replyTo) =>
        replyTo ! GetCityResponse(cities.find(_.name == name))
        Behaviors.same
      case DeleteCity(name, replyTo) =>
        replyTo ! ActionPerformed(s"City $name deleted.")
        registry(cities.filterNot(_.name == name))
    }
}
//#user-registry-actor
