package controllers

import com.typesafe.config.ConfigFactory
import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.utils.Options
import org.slf4j.LoggerFactory
import play.api.libs.ws
import play.api.mvc.{BaseController, ControllerComponents}

import java.util
import javax.inject.Inject

class AgentController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val conf = ConfigFactory.load()
  private val host = conf.getString("Ollama.host")
  private val timeout = conf.getInt("Ollama.request-timeout-seconds")
  private val model = conf.getString("Ollama.model")
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def converse(seed: String): Unit = {
    val ollamaAPI = new OllamaAPI(host)
    ollamaAPI.setRequestTimeoutSeconds(timeout)
    val generateNextQueryPrompt = s"how can you respond to the statement: $seed"
    try {
      val result = ollamaAPI.generate(model, generateNextQueryPrompt, false, new Options(new util.HashMap[String, Object]))
      result.getResponse
    } catch {
      case e: Exception =>
        logger.error("PROCESS FAILED" + e.getMessage)
    }
  }
}
