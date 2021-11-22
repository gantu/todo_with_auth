package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import models.{Session, User}

import javax.inject._
import play.api._
import play.api.mvc._
import service.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import helpers.{UserRequest,UserAction}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val authService: AuthService,
                               val userAction: UserAction) extends BaseController {


  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def priv(): Action[AnyContent] = userAction { userReq: UserRequest[AnyContent] =>
    userReq.user.map(u => Ok(views.html.priv(u))).getOrElse(Ok(views.html.forbidden()))
  }

}
