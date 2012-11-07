package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.Comet
import play.api.libs.Comet.CometMessage
import play.api.templates.Html
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.ws.Response

import play.api.libs.concurrent._
import play.api.Play.current
import akka.util.duration._

import models._

object Importer extends Controller {

  // What does this need to do?
  // Read the file line-by-line while skipping the first line
  // Take each chunk, create a City from it
  // Geocode each City using Bing's WS
  // Insert each City into the DB
  // Send the City back to the client as JSON

  // Create three Enumerators, one for the file, one for the WS, one for the DB
  // When a chunk is computed from one, it gets pushed into the next
  // All three are interleaved in the stream

  def index = Action {
    Ok(views.html.importer.index("MapYourNeighborhood Data Importer"))
  }

  val geocodingEnumerator = Enumerator[City]()

  val dbEnumerator = Enumerator[City]()

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

      val fileLengthEnumerator = Enumerator((fileLength-192)/192)

      val dropColumnNames: Enumeratee[Array[Byte], Array[Byte]] = {
        Enumeratee.drop[Array[Byte]](1)
      }

      val fileEnumerator = Enumerator.fromFile(census.ref.file, 192) through dropColumnNames 

      val toCity: Enumeratee[Array[Byte], City] = Enumeratee.map[Array[Byte]] { arr => City.fromCensus(arr.map(_.toChar).mkString) }

      val cityEnumerator = fileEnumerator through toCity

      val cityToComet = Enumeratee.map[City] { city => 
        // is this where I should do async geocode/db calls?
        Html("""<script>parent.addEntryToTable('""" + City.toJson(city) + """');</script>""")
      }

      Ok.stream(Enumerator.interleave(
        fileLengthEnumerator.andThen(Enumerator.eof).through(fileLengthMessage),
        cityEnumerator.andThen(Enumerator.eof).through(cityToComet)
      ).andThen(Enumerator.eof))
    }.getOrElse{
      Redirect(routes.Importer.index).flashing(
        "error" -> "Missing file"
      )
    }
  }

  def geocodingProgress = Action { 
    
    /*
    val geocodingEnumeratee: Enumeratee[City, Promise[Response]] = Enumeratee.map[City] { city =>
      WS.url(BingGeocoderWS.url(city.state, city.name)).get()
    }
    */

    val geocodeRedeemer = new Iteratee[City, (String, String)] {
    
      def fold1[B](
        done: 
    
    }

    Ok.stream(geocodingEnumerator.andThen(Enumerator.eof).through(geocodingEnumeratee))

  }

  def 


  def monitoring = Action { 
    Ok.stream(
      cityEnumerator

}


object BingGeocoderWS   {

/*  def geocode(state: String, locality: String): Promise[(String, String)] = {
    
  }
  */

  val apiKey = "Arys2Q3zd_PQaE8w9BUXvBY98oeeqg7L_DXoEv3hyk_Gfl_EyMFOgcU0mQNGySq7"

  def url(state: String, locality: String): String = 
    "http://dev.virtualearth.net/REST/v1/Locations?countryRegion=US&" + 
    "adminDistrict=" + state + "&" + 
    "locality=" + locality + "&key=" + apiKey

}
