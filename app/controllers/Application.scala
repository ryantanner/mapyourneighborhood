package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index(".", mapForm))
  }

  def authenticate = Action {
    NotImplemented
  }

  def logout = Action {
    NotImplemented
  }

  def login = Action {
    NotImplemented
  }

  def map = Action {
    Ok(views.html.map.mapContainer("Map Your Neighborhood"))
  }

  val mapForm = Form(
    "location" -> text
  )

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Cities.coordinates
      )
    ).as("text/javascript") 
  }
  
}
