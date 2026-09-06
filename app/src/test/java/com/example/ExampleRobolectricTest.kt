package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.PasswordSecurity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mahima Fashion", appName)
  }

  @Test
  fun `verify password hashing and verification`() {
    val password = "MahimaFashion@2026"
    val salt = PasswordSecurity.generateSalt()
    val hash = PasswordSecurity.hashPassword(password, salt)

    assertTrue(PasswordSecurity.verifyPassword(password, salt, hash))
    assertTrue(!PasswordSecurity.verifyPassword("WrongPassword", salt, hash))
  }
}
