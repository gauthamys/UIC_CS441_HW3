package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

class HealthController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{
  def health: Action[AnyContent] = Action { implicit request =>
    Ok(Json.obj(
      "status" -> "OK"
    ))
  }
}
