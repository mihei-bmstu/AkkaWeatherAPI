package models

import scalikejdbc._

case class CityWeather(city: String, temperature: Float)

object CityWeather extends SQLSyntaxSupport[CityWeather] {
  def apply(rs: WrappedResultSet) = new CityWeather(
    rs.string("city"),
    rs.long("temperature")
  )

}
