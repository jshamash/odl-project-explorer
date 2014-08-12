package controllers

import play.api._
import play.api.mvc._
import org.ops4j.pax.url.mvn.internal.config.MavenRepositoryURL
import scala.xml.XML
import models._
import java.net.URL

object FeaturesController extends Controller {


  def list = Action { implicit request =>
    val xml = scala.xml.XML.load(new URL("http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/org/opendaylight/controller/base-features/1.4.2-SNAPSHOT/base-features-1.4.2-20140805.023708-458-features.xml"))
    val feature = Features.fromXml(xml);
    Ok(views.html.index(feature.toString))
  }



}