package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json.{JsValue, Json}
import utilz.ModelUtil

import javax.inject.Inject

class ModelController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def generate: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject: Option[JsValue] = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            val res = ModelUtil.generateExternal(prompt)
            Ok(Json.obj("result" -> res))
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }


}
