package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.Comet
import play.api.libs.Comet.CometMessage
import play.api.templates.Html
import play.api.libs.json._
import play.api.libs.ws.WS

import play.api.libs.concurrent._
import play.api.Play.current
import akka.util.duration._

import models._

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
      val fileLength = census.ref.file.length

      val fileLengthMessage = Enumeratee.map[Long] { length =>
        Html("""<script>parent.setTotalNumItems(""" + length + """);</script>""")
      }

      val fileLengthEnumerator = Enumerator(fileLength/192)

      //Ok.stream(Enumerator(fileLength/192).andThen(Enumerator.eof).through(fileLengthMessage))

      /* First, post back to the client upon receipt, including basic info
       * Second, post back when lat/long is recieved
       * Third, post back upon successful DB insertion
       * figure out how to indicate failure for the latter two
       */

      lazy val data = Enumerator.fromFile(census.ref.file, 192).map { item => 
        Akka.future {
          val city = City.fromCensus(item.toList.map(_.toChar).mkString.trim)
          Some(City.toJson(city))
        }
      }


        /*
        Promise.timeout({item:Promise[Some[JsValue]] =>
          // create City, insert into DB
          val city = City.fromCensus(item.toList.map(_.toChar).mkString.trim)

          City.insert(city)
          // trigger an asynch akka actor to get lat/long for new city, update row

          Some(City.toJson(city))
          //Some(censusToJson(item.toList.map(_.toChar).mkString.trim))
        }, 100 milliseconds)
        */

      //Promise.timeout(Some(censusToJson(item.toList.map(_.toChar).mkString.trim)), 100 milliseconds)

      val toCometMessage = Enumeratee.map[NotWaiting[Some[JsValue]]] { data => data.fold( //{ value:Redeemed[Some[JsValue]] =>
        onError => { Logger.error("Could not parse: " + data.get.get); Html("ERROR") },
        onSuceess => Html("""<script>parent.addEntryToTable('""" + data.get.get + """');</script>""")
      )}

      Ok.stream(Enumerator.interleave(
        fileLengthEnumerator.andThen(Enumerator.eof).through(fileLengthMessage),
        data.map(_.await(1000)).andThen(Enumerator.eof).through(toCometMessage)
      )) 
    }.getOrElse{
      Redirect(routes.Importer.index).flashing(
        "error" -> "Missing file"
      )
    }
  }


}

object BingGeocoderWS   {

/*  def geocode(state: String, locality: String): Promise[(String, String)] = {
    
  }
  */

  val apiKey = "Arys2Q3zd_PQaE8w9BUXvBY98oeeqg7L_DXoEv3hyk_Gfl_EyMFOgcU0mQNGySq7"

  def URL(state: String, locality: String): String = 
    "http://dev.virtualearth.net/REST/v1/Locations?countryRegion=US&"
    + "adminDistrict=" + state + "&"
    + "locality=" + locality + "&key=" + apiKey

}
