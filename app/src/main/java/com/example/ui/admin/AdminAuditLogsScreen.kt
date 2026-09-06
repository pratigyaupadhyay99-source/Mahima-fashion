package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.SandParchment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminAuditLogsScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
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
          text = "Security & Activity Audit Trail",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(auditLogs, key = { it.id }) { log ->
        val df = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Emerald700.copy(alpha = 0.12f)
              ) {
                Text(
                  text = log.action,
                  color = Emerald700,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = df.format(Date(log.timestamp)),
                fontSize = 9.sp,
                color = Color.Gray
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = log.details,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Emerald900
            )

            if (log.previousValue.isNotEmpty() || log.newValue.isNotEmpty()) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = SandParchment,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp)
              ) {
                Column(modifier = Modifier.padding(6.dp)) {
                  if (log.previousValue.isNotEmpty()) {
                    Text("Prev: ${log.previousValue}", fontSize = 10.sp, color = Color.DarkGray)
                  }
                  if (log.newValue.isNotEmpty()) {
                    Text("New: ${log.newValue}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Emerald700)
                  }
                }
              }
            }

            Text(
              text = "By: ${log.userName} (${log.userRole})",
              fontSize = 10.sp,
              color = Color.Gray,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      }
    }
  }
}
