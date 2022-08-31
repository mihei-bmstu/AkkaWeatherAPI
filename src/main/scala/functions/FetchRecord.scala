package functions

import models.CityWeather
import scalikejdbc._
import system.Properties

object FetchRecord {

  def findCity(city: String): List[CityWeather] = {
    Class.forName("org.postgresql.Driver")
    ConnectionPool.singleton(Properties.urlPG, Properties.userPG, Properties.passPG)

    implicit val session: AutoSession.type = AutoSession
    sql"select city, avg_tmpr_c as temperature from hotel_weather where city = $city limit 1"
      .map(rs => CityWeather(rs)).list().apply()
  }

}
