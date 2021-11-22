package controllers.helpers

import models.User

import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.Inject
import service.AuthService
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserRequest[A](val user: Option[User], request: Request[A]) extends WrappedRequest[A](request)

class UserAction @Inject()(val parser: BodyParsers.Default, authService: AuthService)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]): Future[UserRequest[A]] = {
    request.session.get("myToken") match {
      case None => Future.successful(new UserRequest(None, request))
      case Some(token) => for {
        sess <- authService.getSession(token)
        user <- authService.getUserByUsername(sess.username)
      } yield new UserRequest(Some(user), request)
    }

  }
}