package controllers

import models.{User, UserDAO}

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  val userInMemory = Map("sherlock" -> User("sherlock", "sherlock_pass"), "watson" -> User("watson", "watsonPass"))

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def priv() = Action { implicit request: Request[AnyContent] =>
    val userName = request.queryString("userName")
    val password = request.queryString("password")

    print(s"I got ${userName} and ${password} ")
    val user = UserDAO.getUser(userName(0), password(0), userInMemory)
    user match {
      case Some(u) => Ok(views.html.priv(u))
      case None => Unauthorized(views.html.defaultpages.unauthorized())
    }
  }
}
