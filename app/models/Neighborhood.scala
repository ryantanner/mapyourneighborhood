package models

import play.api.libs.json._

case class Neighborhood(
  id: Long,
  name: String,
  description: String,
  coordinates: Seq[CoordinatePair]
)

// Change from Seq to a class which can enforce a closed polygon?

object Neighborhood {

  var neighborhoods = Set(
    Neighborhood(01L, "Montrose", "A cool place", 
      Seq(CoordinatePair(29.756032, -95.409445), CoordinatePair(29.758119, -95.384382), 
          CoordinatePair(29.738147, -95.410818), CoordinatePair(29.739042, -95.383696)))
/*    Neighborhood(02L, "The Heights", "Good restaurants", 
      Seq(CoordinatePair(01L, "5", "8"), CoordinatePair(02L, "2", "0"))),
    Neighborhood(03L, "Downtown", "Noone goes here", 
      Seq(CoordinatePair(01L, "3", "0"), CoordinatePair(02L, "9", "2"))),
    Neighborhood(04L, "Rice Village", "Nice trees", 
      Seq(CoordinatePair(01L, "5", "4"), CoordinatePair(02L, "2", "6")))*/
  )

  def findAll = this.neighborhoods.toList.sortBy(_.id)

}


case class CoordinatePair(
  lat: Double,
  lng: Double 
)

object CoordinatePair {

  def toJson(coord: CoordinatePair): JsValue = {
    Json.toJson(Seq(coord.lat, coord.lng))
  }

}
