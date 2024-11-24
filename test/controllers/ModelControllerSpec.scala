package controllers

import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.test.Helpers._
import play.api.test._

import javax.inject.Inject

object TestModelService {
  def generateExternal(prompt: String): String = s"External model generated for: $prompt"
  def generate(prompt: String): String = s"Local model generated for: $prompt"
}

class TestModelController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def generateExternalModel: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            val res = TestModelService.generateExternal(prompt)
            Ok(Json.obj("result" -> res))
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }

  def generateLocalModel: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            val res = TestModelService.generate(prompt)
            Ok(Json.obj("result" -> res))
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }
}

class ModelControllerSpec extends PlaySpec {

  "ModelController" should {

    "generateExternalModel should return a result when a valid prompt is provided" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateExternalModel")
        .withJsonBody(Json.obj("prompt" -> "test-prompt"))

      val result = controller.generateExternalModel.apply(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj("result" -> "External model generated for: test-prompt")
    }

    "generateExternalModel should return BadRequest when 'prompt' is missing" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateExternalModel")
        .withJsonBody(Json.obj())

      val result = controller.generateExternalModel.apply(request)

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("error" -> "Missing 'prompt' in request body")
    }

    "generateExternalModel should return BadRequest when JSON is invalid" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateExternalModel")
        .withTextBody("invalid json")

      val result = controller.generateExternalModel.apply(request)

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("error" -> "Invalid JSON in request body")
    }

    "generateLocalModel should return a result when a valid prompt is provided" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateLocalModel")
        .withJsonBody(Json.obj("prompt" -> "test-prompt"))

      val result = controller.generateLocalModel.apply(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj("result" -> "Local model generated for: test-prompt")
    }

    "generateLocalModel should return BadRequest when 'prompt' is missing" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateLocalModel")
        .withJsonBody(Json.obj())

      val result = controller.generateLocalModel.apply(request)

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("error" -> "Missing 'prompt' in request body")
    }

    "generateLocalModel should return BadRequest when JSON is invalid" in {
      val controller = new TestModelController(stubControllerComponents())

      val request = FakeRequest(POST, "/generateLocalModel")
        .withTextBody("invalid json")

      val result = controller.generateLocalModel.apply(request)

      status(result) mustBe BAD_REQUEST
      contentAsJson(result) mustBe Json.obj("error" -> "Invalid JSON in request body")
    }
  }
}
