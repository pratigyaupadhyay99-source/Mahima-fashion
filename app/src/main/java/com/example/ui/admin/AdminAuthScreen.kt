package com.example.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.security.PasswordSecurity
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.SandParchment
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed

@Composable
fun AdminAuthScreen(
  viewModel: FashionViewModel,
  onBackToShop: () -> Unit
) {
  val hasOwner by viewModel.hasOwnerAccount.collectAsStateWithLifecycle()

  // If status is not yet loaded, show loader
  if (hasOwner == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Checking security credentials...", color = Emerald900)
    }
    return
  }

  val isFirstTimeSetup = hasOwner == false

  // Form Fields
  var adminName by remember { mutableStateOf("") }
  var emailOrMobile by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val passValidation = remember(password) {
    PasswordSecurity.validatePassword(password)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Emerald900)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Back to customer shop
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Start
    ) {
      TextButton(
        onClick = onBackToShop,
        colors = ButtonDefaults.textButtonColors(contentColor = GoldLight)
      ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        Spacer(modifier = Modifier.width(4.dp))
        Text("Back to Customer Shop")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Logo & Brand
    Image(
      painter = painterResource(id = R.drawable.ic_mahima_logo),
      contentDescription = "Mahima Fashion Admin Logo",
      modifier = Modifier
        .size(68.dp)
        .clip(RoundedCornerShape(14.dp))
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "MAHIMA FASHION",
      color = GoldLight,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.5.sp
    )

    Text(
      text = if (isFirstTimeSetup) "FIRST-TIME ADMIN ACCOUNT SETUP" else "SECURE ADMIN & STAFF LOGIN",
      color = GoldShimmer,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Auth Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        if (isFirstTimeSetup) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GoldMetallic.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Emerald700,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Welcome Owner! Create your master password once. It will be securely hashed and stored permanently.",
                fontSize = 11.sp,
                color = Emerald900,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = adminName,
            onValueChange = {
              adminName = it
              errorMessage = null
            },
            label = { Text("Owner / Admin Full Name *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Emerald700) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedTextField(
          value = emailOrMobile,
          onValueChange = {
            emailOrMobile = it
            errorMessage = null
          },
          label = { Text("Email Address or Mobile Number *") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Emerald700) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            errorMessage = null
          },
          label = { Text(if (isFirstTimeSetup) "Create Strong Password *" else "Password *") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald700) },
          trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Toggle password"
              )
            }
          },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // First time setup password strength analysis & confirmation
        if (isFirstTimeSetup) {
          Spacer(modifier = Modifier.height(8.dp))

          // Password Strength Bar
          val progress = (passValidation.score / 5f).coerceIn(0f, 1f)
          val strengthColor = when (passValidation.score) {
            0, 1 -> StockRed
            2, 3 -> StockOrange
            4, 5 -> StockGreen
            else -> Color.Gray
          }

          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Password Strength", fontSize = 10.sp, color = Color.Gray)
              Text(
                passValidation.strengthLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = strengthColor
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
              color = strengthColor,
              trackColor = Color.LightGray.copy(alpha = 0.4f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Criteria checklist
          Column(modifier = Modifier.fillMaxWidth()) {
            CriteriaItem("Minimum 8 characters", passValidation.hasMinLength)
            CriteriaItem("Uppercase letter (A-Z)", passValidation.hasUpper)
            CriteriaItem("Lowercase letter (a-z)", passValidation.hasLower)
            CriteriaItem("Number (0-9)", passValidation.hasDigit)
            CriteriaItem("Special character (!@#$)", passValidation.hasSpecial)
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              errorMessage = null
            },
            label = { Text("Confirm Password *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald700) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
        }

        // Error message if any
        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = errorMessage ?: "",
            color = StockRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Button
        Button(
          onClick = {
            if (isFirstTimeSetup) {
              if (adminName.isBlank()) {
                errorMessage = "Please enter Admin/Owner name."
                return@Button
              }
              if (emailOrMobile.isBlank()) {
                errorMessage = "Please enter email or mobile."
                return@Button
              }
              if (!passValidation.isValid) {
                errorMessage = "Password does not meet all security requirements."
                return@Button
              }
              if (password != confirmPassword) {
                errorMessage = "Passwords do not match."
                return@Button
              }

              viewModel.registerOwner(
                name = adminName,
                emailOrMobile = emailOrMobile,
                password = password,
                onSuccess = {},
                onError = { errorMessage = it }
              )
            } else {
              if (emailOrMobile.isBlank() || password.isBlank()) {
                errorMessage = "Please enter your email/mobile and password."
                return@Button
              }

              viewModel.loginAdmin(
                emailOrMobile = emailOrMobile,
                password = password,
                onSuccess = {},
                onError = { errorMessage = it }
              )
            }
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = null,
            tint = GoldShimmer
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isFirstTimeSetup) "CREATE OWNER ACCOUNT & PROCEED" else "LOGIN TO ADMIN DASHBOARD",
            color = GoldShimmer,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@Composable
fun CriteriaItem(text: String, isMet: Boolean) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 1.dp)
  ) {
    Icon(
      imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
      contentDescription = null,
      tint = if (isMet) StockGreen else Color.LightGray,
      modifier = Modifier.size(12.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      fontSize = 10.sp,
      color = if (isMet) StockGreen else Color.Gray,
      fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
    )
  }
}
