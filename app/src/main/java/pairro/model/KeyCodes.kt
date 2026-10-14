package pairro.model

object KeyCodes {
  const val VK_BACK_SPACE = 8
  const val VK_TAB = 9
  const val VK_ENTER = 10
  const val VK_SHIFT = 16
  const val VK_CONTROL = 17
  const val VK_ALT = 18
  const val VK_ESCAPE = 27
  const val VK_SPACE = 32
  const val VK_DELETE = 127
  const val VK_WINDOWS = 524

  const val VK_LEFT = 37
  const val VK_UP = 38
  const val VK_RIGHT = 39
  const val VK_DOWN = 40

  const val VK_COMMA = 44
  const val VK_MINUS = 45
  const val VK_PERIOD = 46
  const val VK_SLASH = 47
  const val VK_SEMICOLON = 59
  const val VK_EQUALS = 61
  const val VK_OPEN_BRACKET = 91
  const val VK_BACK_SLASH = 92
  const val VK_CLOSE_BRACKET = 93
  const val VK_BACK_QUOTE = 192
  const val VK_QUOTE = 222

  fun vkForDigit(d: Int): Int = 48 + d
  fun vkForLetter(c: Char): Int = c.uppercaseChar().code
  fun vkForFunctionKey(n: Int): Int = 111 + n

  fun forChar(c: Char): Pair<Int, Boolean>? {
    return when (c) {
      in 'a'..'z' -> vkForLetter(c) to false
      in 'A'..'Z' -> vkForLetter(c) to true
      in '0'..'9' -> vkForDigit(c - '0') to false
      ' ' -> VK_SPACE to false
      '\n' -> VK_ENTER to false
      '\t' -> VK_TAB to false
      ',' -> VK_COMMA to false
      '.' -> VK_PERIOD to false
      '/' -> VK_SLASH to false
      '?' -> VK_SLASH to true
      ';' -> VK_SEMICOLON to false
      ':' -> VK_SEMICOLON to true
      '=' -> VK_EQUALS to false
      '+' -> VK_EQUALS to true
      '-' -> VK_MINUS to false
      '_' -> VK_MINUS to true
      '[' -> VK_OPEN_BRACKET to false
      '{' -> VK_OPEN_BRACKET to true
      ']' -> VK_CLOSE_BRACKET to false
      '}' -> VK_CLOSE_BRACKET to true
      '\\' -> VK_BACK_SLASH to false
      '|' -> VK_BACK_SLASH to true
      '\'' -> VK_QUOTE to false
      '"' -> VK_QUOTE to true
      '`' -> VK_BACK_QUOTE to false
      '~' -> VK_BACK_QUOTE to true
      '!' -> vkForDigit(1) to true
      '@' -> vkForDigit(2) to true
      '#' -> vkForDigit(3) to true
      '$' -> vkForDigit(4) to true
      '%' -> vkForDigit(5) to true
      '^' -> vkForDigit(6) to true
      '&' -> vkForDigit(7) to true
      '*' -> vkForDigit(8) to true
      '(' -> vkForDigit(9) to true
      ')' -> vkForDigit(0) to true
      else -> null
    }
  }
}