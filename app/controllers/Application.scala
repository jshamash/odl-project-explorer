package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def details = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def features = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def projects = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def components = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }


}