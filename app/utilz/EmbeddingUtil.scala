package utilz

import java.util
import scala.io.Source
import scala.util.Using

object EmbeddingUtil {
  private def convertToArr(v: String): Array[Double] = {
    v.stripPrefix("[").stripSuffix("]").split(",").map(_.trim.toDouble)
  }

  def loadEmbeddings(embeddingPath: String): util.HashMap[String, Array[Double]] = {
    val lookup = new util.HashMap[String, Array[Double]]()
    val reverseLookup = new util.HashMap[Array[Double], String]()
    Using.resource(Source.fromFile(embeddingPath)) { source =>
      for (line <- source.getLines()) {
        val kv = line.split("\t")
        if (kv.length == 2) {
          val word = kv(0)
          val embedding = EmbeddingUtil.convertToArr(kv(1))
          lookup.put(word, embedding)
          reverseLookup.put(embedding, word)
        }
      }
    }

    lookup
  }

  def candidates(outputArray: Array[Double], lookup: util.HashMap[String, Array[Double]]): String = {
    var closestWord: String = ""
    var maxSimilarity: Double = Double.MinValue

    lookup.forEach { (word, embedding) =>
      val similarity = cosineSimilarity(outputArray, embedding)
      if (similarity > maxSimilarity) {
        maxSimilarity = similarity
        closestWord = word
      }
    }
    closestWord
  }

  private def cosineSimilarity(arr1: Array[Double], arr2: Array[Double]): Double = {
    val dotProduct = arr1.zip(arr2).map { case (x, y) => x * y }.sum
    val magnitude1 = math.sqrt(arr1.map(x => x * x).sum)
    val magnitude2 = math.sqrt(arr2.map(x => x * x).sum)
    dotProduct / (magnitude1 * magnitude2)
  }
}
