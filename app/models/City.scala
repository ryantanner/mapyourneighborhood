package models

import play.api.libs.json._

case class City(
  id: Long,
  name: String,
  neighborhoods: Set[Neighborhood],
  center: CoordinatePair
)

object City {

  val sampleCity = City(01L, "Houston", Neighborhood.neighborhoods, CoordinatePair(01L, 29.756032, -95.409445))

  val cities = Map[String,City]("Houston" -> sampleCity)

  def cityByName(cityName: String): Option[City] = cities.get(cityName)

  def toJson(city: City): JsValue = {
    import play.api.libs.json._

    Json.toJson(Map(
      "id" -> Json.toJson(city.id),
      "name" -> Json.toJson(city.name),
      "coordinates" -> CoordinatePair.toJson(city.center),
      "neighborhoods" -> Json.toJson(Seq(
        city.neighborhoods map { neighborhood => Map(
          "id" -> Json.toJson(neighborhood.id),
          "name" -> Json.toJson(neighborhood.name),
          "description" -> Json.toJson(neighborhood.description),
          "coordinates" -> Json.toJson(neighborhood.coordinates.map(CoordinatePair.toJson))
        )}
      ))
    ))
  }

}
