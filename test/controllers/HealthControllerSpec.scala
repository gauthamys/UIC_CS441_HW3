package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class HealthControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HealthController" should {

    "return OK with status message" in {
      // Arrange: Inject the controller
      val controller = inject[controllers.HealthController]
      val request = FakeRequest(GET, "/health")

      // Act: Call the health action
      val result = controller.health().apply(request)

      // Assert: Verify the response
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj("status" -> "OK")
    }
  }
}