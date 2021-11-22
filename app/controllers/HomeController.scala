package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import models.{Session, User}

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


  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def priv(): Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
    val sessionFromHeader = request.session.get("myToken")
    sessionFromHeader match {
      case None => Future {Unauthorized(views.html.defaultpages.unauthorized())}
      case Some(token) => extractUser(token) map { user =>
        Ok(views.html.priv(user))
      }
    }
  }

  def extractUser(token: String): Future[User] = {
    for {
      session <- authService.getSession(token)
      username = session.username
      user <- authService.getUserByUsername(username)
    } yield user
  }
}
