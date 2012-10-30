package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.Comet
import play.api.libs.Comet.CometMessage
import play.api.templates.Html
import play.api.libs.json._

import play.api.libs.concurrent._

import akka.util.duration._

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

      Ok.stream(Enumerator("Processing \"" + filename + "\"...").andThen(Enumerator.eof))

      lazy val data = Enumerator.fromFile(census.ref.file, 192).map { item => 
        Promise.timeout(Some(censusToJson(item.toList.map(_.toChar).mkString.trim)), 100 milliseconds)
      }

      val toCometMessage = Enumeratee.map[Promise[Some[JsValue]]] { data => data.value.fold( //{ value:Redeemed[Some[JsValue]] =>
        onError => Html("ERROR"),
        onSuceess => Html("""<script>parent.addEntryToTable('""" + data.value.get.get + """');</script>""")
      )}

      Ok.stream(data.andThen(Enumerator.eof).through(toCometMessage)) 
    }.getOrElse{
      Redirect(routes.Importer.index).flashing(
        "error" -> "Missing file"
      )
    }
  } 

  private def censusToJson(censusItem: String): JsValue = {

    val item = censusItem.toList
  
    Json.toJson(
      Map(
        "uace"          -> item.slice(0,5).mkString.trim,
        "name"          -> item.slice(10,70).mkString.trim,
        "pop"           -> item.slice(76,84).mkString.trim,
        "hu"            -> item.slice(90,98).mkString.trim,
        "arealand"      -> item.slice(104,117).mkString.trim,
        "arealandsqmi"  -> item.slice(123,131).mkString.trim,
        "areawater"     -> item.slice(137,150).mkString.trim,
        "areawatersqmi" -> item.slice(156,164).mkString.trim,
        "popden"        -> item.slice(170,178).mkString.trim,
        "lsadc"         -> item.slice(183,185).mkString.trim
      )
    )

  }

}
