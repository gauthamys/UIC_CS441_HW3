package agent

import com.typesafe.config.ConfigFactory
import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.utils.Options
import org.slf4j.LoggerFactory
import service.ModelService

import java.io.{BufferedWriter, File, FileWriter}
import java.util

object ConversationalAgent {
  private val conf = ConfigFactory.load()
  private val host = conf.getString("Ollama.host")
  private val model = conf.getString("Ollama.model")
  private val timeout = conf.getInt("Ollama.request-timeout-seconds")
  private val numReplies = conf.getInt("Conversation.numReplies")
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def main(args: Array[String]): Unit = {
    val ollamaApi = new OllamaAPI(host)
    ollamaApi.setRequestTimeoutSeconds(timeout)
    val prompt = s"how can you respond to the statement: ${args.mkString(" ")}"
    var i = 0
    var inter = prompt
    logger.info(s"Starting conversation with prompt $inter... ")

    val resultsDir = new File("results")
    if (!resultsDir.exists()) {
      resultsDir.mkdirs()
    }

    val fileWriter = new BufferedWriter(new FileWriter("results/conversation.txt", true))
    try {
      fileWriter.write(s"\n\n\n\n-----------------Starting conversation with prompt: $prompt---------------------------\n")
      while(i < numReplies) {
        Thread.sleep(60 * 1000)
        inter = ModelService.generateExternal(inter)
        logger.info(s"Claude: $inter")
        fileWriter.write("--------------------Claude----------------------\n")
        fileWriter.write(s"$inter\n\n")

        inter = ollamaApi.generate(model, inter, false, new Options(new util.HashMap[String, Object])).getResponse
        logger.info(s"Ollama: $inter")
        fileWriter.write("--------------------Ollama----------------------\n")
        fileWriter.write(s"$inter\n\n")

        i = i + 1
      }
    } finally {
      fileWriter.close()
    }
  }
}
