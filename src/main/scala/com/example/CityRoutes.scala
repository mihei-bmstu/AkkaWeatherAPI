package com.example


import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.example.Registry._
import WeatherAPI.WeatherRequest.getTemp

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.duration.DurationInt


class CityRoutes (cityRegistry: ActorRef[Registry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  //private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private implicit val timeout: Timeout = 3.seconds

  def getCities(): Future[Cities] =
    cityRegistry.ask(GetCities)
  def getCity(name: String): Future[GetCityResponse] =
    cityRegistry.ask(GetCity(name, _))
  def insertCity(city: City): Future[ActionPerformed] =
    cityRegistry.ask(InsertCity(city, _))
  def deleteCity(name: String): Future[ActionPerformed] =
    cityRegistry.ask(DeleteCity(name, _))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val cityRoutes: Route =
  pathPrefix("cities") {
    concat(
      //#users-get-delete
      pathEnd {
        concat(
          get {
            complete(getCities())
          },
          post {
            entity(as[City]) { city =>
              onSuccess(insertCity(city)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          })
      },
      //#users-get-delete
      //#users-get-post
      path(Segment) { name =>
        concat(
          get {
            //#retrieve-user-info
            rejectEmptyResponse {
              onSuccess(getCity(name)) { response =>
                complete(response.maybeCity)
              }
            }
            //#retrieve-user-info
          },
          delete {
            //#users-delete-logic
            onSuccess(deleteCity(name)) { performed =>
              complete((StatusCodes.OK, performed))
            }
            //#users-delete-logic
          })
      })
    //#users-get-delete
  }
  //#all-routes
}
