package controllers

import models.{User, Session}

import javax.inject._
import play.api._
import play.api.mvc._
import service.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, authService: AuthService) extends BaseController {


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def extractUser(request: RequestHeader): Future[Session] = {
    val sessionFromHeader = request.session.get("myToken")

    val user: Future[Session] = for {
      token <- sessionFromHeader
      session <- authService.getSession(token)
      username = session.username
      user <- authService.getUserByUsername(username)
    } yield user
    user
  }


}
