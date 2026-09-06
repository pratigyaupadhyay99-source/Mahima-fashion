package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.security.PasswordSecurity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed

@Composable
fun AdminSettingsScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val currentUser by viewModel.currentAdminUser.collectAsStateWithLifecycle()

  var currentPassword by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var passError by remember { mutableStateOf<String?>(null) }

  val passValidation = remember(newPassword) {
    PasswordSecurity.validatePassword(newPassword)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
  ) {
    Surface(
      color = Emerald900,
      shadowElevation = 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldLight)
        }
        Text(
          text = "Business Profile & Security",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Column(modifier = Modifier.padding(16.dp)) {
      // 1. Business Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Business & GST Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = "MAHIMA FASHION",
            onValueChange = {},
            readOnly = true,
            label = { Text("Business Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = "24AAACM1234F1Z8",
            onValueChange = {},
            readOnly = true,
            label = { Text("GSTIN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = "+91 9327607195",
            onValueChange = {},
            readOnly = true,
            label = { Text("Registered Wholesale Mobile & Message (WhatsApp)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = "2070, New Pashupati Textile Market, Opp. Shyam Market, Moti Begam Wadi, Ring Road, Surat, Gujarat",
            onValueChange = {},
            readOnly = true,
            label = { Text("Shop Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Change Password Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Change Admin Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
          Text("Active account: ${currentUser?.name ?: "Admin"}", fontSize = 11.sp, color = Color.Gray)

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = currentPassword,
            onValueChange = {
              currentPassword = it
              passError = null
            },
            label = { Text("Current Password *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = newPassword,
            onValueChange = {
              newPassword = it
              passError = null
            },
            label = { Text("New Strong Password *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          if (newPassword.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            val progress = (passValidation.score / 5f).coerceIn(0f, 1f)
            val strengthColor = when (passValidation.score) {
              0, 1 -> StockRed
              2, 3 -> StockOrange
              4, 5 -> StockGreen
              else -> Color.Gray
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Strength", fontSize = 10.sp, color = Color.Gray)
              Text(passValidation.strengthLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = strengthColor)
            }
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
              color = strengthColor,
              trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = {
              confirmNewPassword = it
              passError = null
            },
            label = { Text("Confirm New Password *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          if (passError != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(passError ?: "", color = StockRed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              if (currentPassword.isBlank()) {
                passError = "Please enter current password."
                return@Button
              }
              if (!passValidation.isValid) {
                passError = "New password must be at least 8 chars with uppercase, lowercase, number, and special character."
                return@Button
              }
              if (newPassword != confirmNewPassword) {
                passError = "Passwords do not match."
                return@Button
              }

              viewModel.changePassword(
                currentPass = currentPassword,
                newPass = newPassword,
                onSuccess = {
                  currentPassword = ""
                  newPassword = ""
                  confirmNewPassword = ""
                  passError = null
                },
                onError = { passError = it }
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldShimmer, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Update Password Securely", color = GoldShimmer, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Database & App Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald800.copy(alpha = 0.05f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("System & Persistence Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald900)
          Spacer(modifier = Modifier.height(4.dp))
          Text("• SQLite Room Database: mahima_fashion.db (Operational)", fontSize = 11.sp, color = Color.DarkGray)
          Text("• Password Security: Salted SHA-256 with constant-time verification", fontSize = 11.sp, color = Color.DarkGray)
          Text("• App Version: 1.0.0 (Production Release)", fontSize = 11.sp, color = Color.DarkGray)
        }
      }
    }
  }
}
