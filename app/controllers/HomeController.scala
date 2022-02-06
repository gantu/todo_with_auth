package controllers

import javax.inject._
import play.api.mvc._
import service.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import helpers.{UserAction, UserRequest}
import com.rallyhealth.weejson.v1.jackson.{FromJson, ToJson}
import com.rallyhealth.weepickle.v1.WeePickle.{FromScala, FromTo, ToScala, macroFromTo}
import com.rallyhealth.weepickle.v1.core.TransformException
import models.{LoginResponse, User}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val authService: AuthService,
                               val userAction: UserAction) extends BaseController {

  implicit val rw: FromTo[User] = macroFromTo
  implicit val lrrw: FromTo[LoginResponse] = macroFromTo

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def priv(): Action[AnyContent] = userAction { userReq: UserRequest[AnyContent] =>
    userReq.user.map(u => Ok(views.html.priv(u))).getOrElse(Ok(views.html.forbidden()))
  }

  def verifyToken(token: String) = Action.async { implicit request: Request[AnyContent] =>
    val session = authService.getSession(token)
    session.map(s => Ok(FromScala(LoginResponse(s.token, s.username)).transform(ToJson.string)))
  }

  def login = Action.async { implicit request: Request[AnyContent] =>
    val userDto = request.body.asJson
    val userObject = userDto match {
      case Some(value) => FromJson(value.toString).transform(ToScala[User])
      case _ => throw new RuntimeException
    }
    println("Login attempt: "+userObject.username)
    val token = for {
      user <- authService.getUserByUsernameAndPassword(userObject.username, userObject.password)
      optToken <-
        user match {
          case None => Future.successful(None)
          case Some(u) => authService.generateToken(u.username)
        }
    } yield optToken

    token.map { optionalT =>
      optionalT.map(t =>
        Ok(FromScala(LoginResponse(t.token, t.username))
          .transform(ToJson.string))
          .withSession(request.session + ("myToken" -> t.token)))
        .getOrElse(Unauthorized(views.html.defaultpages.unauthorized()).withNewSession)
    }
  }
}
