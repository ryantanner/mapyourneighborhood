import views.html.helper._
import views.html._

package object MyHelpers {
    
  implicit val twitterBootstrapInlineField = new FieldConstructor { 
    def apply(elts: FieldElements) = twitterBootstrapInlineFieldConstructor(elts)
  }
    
}
