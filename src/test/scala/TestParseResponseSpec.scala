import org.scalatest._
import WeatherAPI.WeatherRequest._
import ujson.{IncompleteParseException, ParseException}

class TestParseResponseSpec extends UnitSpec {
  test("Empty string test") {
    val emptyResponse = ""
    assertThrows[IncompleteParseException](parseResponse(emptyResponse))
  }
  test("Random string test") {
    val emptyResponse = "fgdsserews"
    assertThrows[ParseException](parseResponse(emptyResponse))
  }
  test("Correct response parsing") {
    val correctResponse = """{"success":true,"error":null,"response":{"id":"UWWW","dataSource":"METAR_NOAA","loc":
                            |{"long":50.166666666667,"lat":53.5},"place":{"name":"kurumoch","city":"kurumoch",
                            |"state":"","country":"ru"},"profile":{"tz":"Europe\/Samara","tzname":"+04","tzoffset":14400,
                            |"isDST":false,"elevM":146,"elevFT":479},"obTimestamp":1661941800,
                            |"obDateTime":"2022-08-31T14:30:00+04:00","ob":{"type":"station",
                            |"timestamp":1661941800,"dateTimeISO":"2022-08-31T14:30:00+04:00",
                            |"recTimestamp":1661942511,"recDateTimeISO":"2022-08-31T14:41:51+04:00",
                            |"tempC":31,"tempF":88,"dewpointC":12,"dewpointF":54,"humidity":31,
                            |"pressureMB":1008,"pressureIN":29.76,"spressureMB":992,"spressureIN":29.28,
                            |"altimeterMB":1009,"altimeterIN":29.79,"windKTS":10,"windKPH":19,"windMPH":12,
                            |"windSpeedKTS":10,"windSpeedKPH":19,"windSpeedMPH":12,"windDirDEG":230,"windDir":"SW",
                            |"windGustKTS":19,"windGustKPH":35,"windGustMPH":22,"flightRule":"VFR",
                            |"visibilityKM":9.99402624,"visibilityMI":6.21,"weather":"Mostly Sunny",
                            |"weatherShort":"Mostly Sunny","weatherCoded":"::FW","weatherPrimary":"Mostly Sunny",
                            |"weatherPrimaryCoded":"::FW","cloudsCoded":"FW","icon":"fair.png","heatindexC":31.1,
                            |"heatindexF":88,"windchillC":31.1,"windchillF":88,"feelslikeC":31.1,"feelslikeF":88,
                            |"isDay":true,"sunrise":1661910401,"sunriseISO":"2022-08-31T05:46:41+04:00",
                            |"sunset":1661959962,"sunsetISO":"2022-08-31T19:32:42+04:00","snowDepthCM":null,
                            |"snowDepthIN":null,"precipMM":0,"precipIN":0,"solradWM2":606,
                            |"solradMethod":"estimated","ceilingFT":null,"ceilingM":null,"light":70,"uvi":null,
                            |"QC":"O","QCcode":10,"trustFactor":100,"sky":19},
                            |"raw":"METAR UWWW 311030Z 23005G10MPS 210V270 CAVOK 31\/12 Q1009 R15\/CLRD60 NOSIG RMK QFE745\/0994",
                            |"relativeTo":{"lat":53.20007,"long":50.15,"bearing":2,"bearingENG":"N","distanceKM":33.369,
                            |"distanceMI":20.735}}}
                            |""".stripMargin
    assert(parseResponse(correctResponse) === "31.0")
  }
  test("Error response parsing") {
    val errorResponse = """{"success":false,"error":{"code":"invalid_location",
                            |"description":"The requested location was not found."},"response":[]}
                            |""".stripMargin
    assert(parseResponse(errorResponse) === "The requested location was not found.")
  }

}
