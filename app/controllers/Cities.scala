package controllers

import play.api._
import play.api.mvc._

import models._

import play.api.libs.json._

object Cities extends Controller {

  def listAll = Action {
    NotImplemented
  }

  def single(uace: Int) = Action {
    NotImplemented
  }

  def byName(cityName: String) = Action {
    City.cityByName(cityName) match {
      case Some(city) => Ok(views.html.map.city(city))
      case None => NotFound("This city doesn't exist!")
    }
  }

  def coordinates(cityName: String) = Action {
    City.cityByName(cityName) match {
      case Some(city) => Ok(Json.toJson(Map("city" -> Json.toJson(cityName),
                                            "coordinates" -> Json.toJson(Seq(city.center.lat, city.center.lng)))))
      case None => NotFound("This city doesn't exist!")
    }
  }

  def city(cityName: String) = Action {
    City.cityByName(cityName) match {
      case Some(city) => Ok(City.toJson(city))
      case None => NotFound("City does not exist")
    }
  }
    

}
