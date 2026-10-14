package pairro.protocol

object Protocol {
  private const val FIELD_SEP = "|"
  private const val KV_SEP = "="

  fun encode(type: String, fields: Map<String, String> = emptyMap()): String {
    val sb = StringBuilder(type)

    fields.forEach { (k, v) ->
      sb.append(FIELD_SEP).append(k).append(KV_SEP).append(escape(v))
    }

    return sb.toString()
  }

  fun decode(message: String): Pair<String, Map<String, String>> {
    val parts = message.split(FIELD_SEP)
    val fields = parts.drop(1).mapNotNull {
      val idx = it.indexOf(KV_SEP)

      if (idx < 0) null else it.substring(0, idx) to unescape(it.substring(idx + 1))
    }.toMap()

    return (parts.firstOrNull() ?: "") to fields
  }

  private fun escape(value: String): String =
    value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n")

  private fun unescape(value: String): String =
    value.replace("\\n", "\n").replace("\\p", "|").replace("\\\\", "\\")
}