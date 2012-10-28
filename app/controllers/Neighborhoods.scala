package controllers

import play.api._
import play.api.mvc._

import models._

object Neighborhoods extends Controller {

  def listAll(uace: Int) = Action {
    NotImplemented
  }

  def city(cityName: String) = Action {
    City.cityByName(cityName) match {
      case Some(city) => Ok(views.html.map.city(city))
      case None => NotFound("This city doesn't exist!")
    }
  }

  def single(uace: Int, neighborhood: String) = Action {
    NotImplemented
  }

  def create(uace: Int) = Action {
    NotImplemented
  }

}

