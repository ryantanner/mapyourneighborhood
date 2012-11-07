package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.libs.json._
import play.api.Play.current

import java.math.BigDecimal

case class City(
  id: Pk[Long] = NotAssigned,
  uace: Int,
  name: String,
  state: String,
  population: Int,
  populationDensity: Double,
  latitude: BigDecimal,
  longitude: BigDecimal
)

object City {

  // Parse a single city
  val simple = {
    get[Pk[Long]]("city.id") ~
    get[Int]("city.uace") ~
    get[String]("city.name") ~
    get[String]("city.state") ~
    get[Int]("city.population") ~
    get[Double]("city.population_density") ~
    get[BigDecimal]("city.latitude") ~
    get[BigDecimal]("city.longitude") map {
      case id~uace~name~state~population~populationDensity~latitude~longitude =>
        City(id, uace, name, state, population, populationDensity, latitude, longitude)
    }
  }

  // Retrieve by ID
  def findById(id: Long): Option[City] = {
    DB.withConnection { implicit connection =>
      SQL("select * from cities where id = {id}").on('id -> id).as(City.simple.singleOpt)
    }
  }

  // Retrieve by UACE
  def findByUACE(uace: Long): Option[City] = {
    DB.withConnection { implicit connection =>
      SQL("select * from cities where uace = {uace}").on('uace -> uace).as(City.simple.singleOpt)
    }
  }

  // Insert new city
  def insert(city: City) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into cities values (
            (select next value for city_seq),
            {uace}, {name}, {population}, {populationDensity}, {latitude}, {longitude}
          )
        """
      ).on(
        'uace -> city.uace,
        'name -> city.name,
        'population -> city.population,
        'populationDensity -> city.populationDensity,
        'latitude -> city.latitude,
        'longitude -> city.longitude
      ).executeUpdate()
    }
  }

  def toJson(city: City): JsValue = {
    import play.api.libs.json._

    Json.toJson(Map(
      "id" -> Json.toJson(city.id.getOrElse(0L)),
      "uace" -> Json.toJson(city.uace),
      "name" -> Json.toJson(city.name.replaceAll("'","&#39;")),
      "state" -> Json.toJson(city.state),
      "population" -> Json.toJson(city.population),
      "populationDensity" -> Json.toJson(city.populationDensity),
      "latitude" -> Json.toJson(city.latitude.doubleValue),
      "longitude" -> Json.toJson(city.longitude.doubleValue)
/*      "neighborhoods" -> Json.toJson(Seq(
        city.neighborhoods map { neighborhood => Map(
          "id" -> Json.toJson(neighborhood.id),
          "name" -> Json.toJson(neighborhood.name),
          "description" -> Json.toJson(neighborhood.description),
          "coordinates" -> Json.toJson(neighborhood.coordinates.map(CoordinatePair.toJson))
        )}
      ))*/
    ))
  }

  def fromCensus(censusItem: String): City = {
    val item = censusItem.toList

    val name = item.slice(10,70).mkString.trim

    City(
      uace = item.slice(0,5).mkString.trim.toInt,
      name = name.split(",")(0),
      state = name.split(",")(1),
      population = item.slice(76,84).mkString.trim.toInt,
      populationDensity = item.slice(170,178).mkString.trim.toDouble,
      latitude = new BigDecimal(1.0),
      longitude = new BigDecimal(1.0)
    )
  }
    

}
