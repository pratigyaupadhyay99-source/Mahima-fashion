package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordSecurity {

  data class PasswordValidationResult(
    val isValid: Boolean,
    val hasMinLength: Boolean,
    val hasUpper: Boolean,
    val hasLower: Boolean,
    val hasDigit: Boolean,
    val hasSpecial: Boolean,
    val score: Int, // 0 to 5
    val strengthLabel: String
  )

  fun validatePassword(password: String): PasswordValidationResult {
    val hasMinLength = password.length >= 8
    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    var score = 0
    if (hasMinLength) score++
    if (hasUpper) score++
    if (hasLower) score++
    if (hasDigit) score++
    if (hasSpecial) score++

    val label = when (score) {
      0, 1 -> "Very Weak"
      2 -> "Weak"
      3 -> "Medium"
      4 -> "Strong"
      5 -> "Very Strong"
      else -> "Weak"
    }

    val isValid = hasMinLength && hasUpper && hasLower && hasDigit && hasSpecial

    return PasswordValidationResult(
      isValid = isValid,
      hasMinLength = hasMinLength,
      hasUpper = hasUpper,
      hasLower = hasLower,
      hasDigit = hasDigit,
      hasSpecial = hasSpecial,
      score = score,
      strengthLabel = label
    )
  }

  fun generateSalt(): String {
    val random = SecureRandom()
    val saltBytes = ByteArray(16)
    random.nextBytes(saltBytes)
    return saltBytes.joinToString("") { "%02x".format(it) }
  }

  fun hashPassword(password: String, salt: String): String {
    val input = "$salt:$password:MahimaFashionWholesaleSecureSalt2026"
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(input.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
  }

  fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
    val computed = hashPassword(password, salt)
    return computed == expectedHash
  }
}
