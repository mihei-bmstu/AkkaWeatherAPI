import WeatherAPI.WeatherRequest._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.model.headers.RawHeader

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class TestSendRequestSpec extends UnitSpec {

  val errorRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = "https://aerisweather1.p.rapidapi.com/observations/" + "moscowfdsf,ru"
  )
    .withHeaders(
      RawHeader("X-RapidAPI-Key", "f452e61143msh123aa1934dc90a6p12848bjsn8b09796dd8a2"),
      RawHeader("X-RapidAPI-Host", "aerisweather1.p.rapidapi.com")
    )

  val errorResponse: String =
  """{"success":false,"error":{"code":"invalid_location","description":"The requested location was not found."},"response":[]}"""

  test("Received wrong request") {
    val resultFuture = sendRequest(errorRequest)
    val result = Await.result(resultFuture, 5.seconds)
    assert(result == errorResponse)

  }

}
