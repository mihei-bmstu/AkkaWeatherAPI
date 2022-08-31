package system

import java.util.Properties

object Properties {
  val userPG = "user"
  val passPG = "user"
  val urlPG = "jdbc:postgresql://localhost:5432/demo"
  val tablePGHotelWeather = "hotel_weather"

  val propertiesPG = new Properties()
  propertiesPG.setProperty("user", userPG)
  propertiesPG.setProperty("password", passPG)
  propertiesPG.setProperty("driver", "org.postgresql.Driver")

}
