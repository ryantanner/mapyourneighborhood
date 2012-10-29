package controllers

import play.api._
import play.api.mvc._

object Importer extends Controller {

  def index = Action {
    Ok(views.html.importer.index())
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("census-data").map { census =>
      import java.io.{File}
      import io.Source

      val filename = census.filename
      val contentType = census.contentType

      val stream:Iterator[String] = Source.fromFile(census.ref.file).getLines 

      val firstLine = stream.next

      Ok("file upload: " + firstLine)
    }.getOrElse{
      Redirect(routes.Importer.index).flashing(
        "error" -> "Missing file"
      )
    }
  } 


}
