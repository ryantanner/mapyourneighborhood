package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.Comet
import play.api.libs.Comet.CometMessage
import play.api.templates.Html

object Importer extends Controller {

  def index = Action {
    Ok(views.html.importer.index("MapYourNeighborhood Data Importer"))
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("census-data").map { census =>
      import java.io.{File, FileInputStream}
      import io.Source

      val filename = census.filename
      val contentType = census.contentType

      lazy val data = Enumerator.fromFile(census.ref.file, 192)

      val toCometMessage = Enumeratee.map[Array[Byte]] { data =>
        Html("""<script>console.log('""" + data.toList.map(_.toChar).mkString.trim + """')</script>""")
      }

      Ok.stream(data.andThen(Enumerator.eof).through(toCometMessage)) 
    }.getOrElse{
      Redirect(routes.Importer.index).flashing(
        "error" -> "Missing file"
      )
    }
  } 

}
