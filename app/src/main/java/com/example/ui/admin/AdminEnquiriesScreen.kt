package com.example.ui.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderEnquiryEntity
import com.example.ui.FashionViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEnquiriesScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val enquiries by viewModel.enquiries.collectAsStateWithLifecycle()

  var selectedStatusFilter by remember { mutableStateOf("All") }

  val filtered = if (selectedStatusFilter == "All") {
    enquiries
  } else {
    enquiries.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
  }

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
          text = "Wholesale Enquiries & Orders",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Status Filter Tabs
    val statuses = listOf("All", "New", "Contacted", "Confirmed", "Completed", "Cancelled")
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(statuses) { status ->
        val count = if (status == "All") enquiries.size else enquiries.count { it.status == status }
        val isSelected = selectedStatusFilter == status
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isSelected) Emerald700 else MaterialTheme.colorScheme.surface,
          shadowElevation = 1.dp,
          modifier = Modifier.padding(2.dp)
        ) {
          Text(
            text = "$status ($count)",
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .background(Color.Transparent)
              .clickable { selectedStatusFilter = status }
          )
        }
      }
    }

    if (filtered.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("No enquiries in this status.", color = Color.Gray, fontSize = 14.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filtered, key = { it.id }) { enquiry ->
          EnquiryAdminCard(
            enquiry = enquiry,
            onUpdateStatus = { newStatus ->
              viewModel.updateEnquiryStatus(enquiry.id, newStatus)
              // If marked confirmed/completed, adjust inventory
              if (newStatus == "Confirmed" || newStatus == "Completed") {
                viewModel.adjustStock(
                  productId = enquiry.productId,
                  delta = -enquiry.quantity,
                  reason = "Fulfilled Wholesale Order #${enquiry.id}"
                )
              }
            },
            onCall = {
              val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${enquiry.mobile}")
              }
              context.startActivity(intent)
            },
            onWhatsApp = {
              val msg = "Hello ${enquiry.customerName}, regarding your wholesale order enquiry for ${enquiry.quantity} pcs of ${enquiry.productName} at Mahima Fashion..."
              val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=${enquiry.mobile.replace("+", "").replace(" ", "")}&text=${Uri.encode(msg)}")
              }
              context.startActivity(intent)
            }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquiryAdminCard(
  enquiry: OrderEnquiryEntity,
  onUpdateStatus: (String) -> Unit,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  var expandedStatusMenu by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Enquiry #${enquiry.id}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldDark
          )
          Text(
            text = enquiry.businessName.ifBlank { "Retail Buyer" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
          )
        }

        // Status Dropdown
        val (badgeBg, badgeFg) = when (enquiry.status) {
          "New" -> StockOrange.copy(alpha = 0.15f) to StockOrange
          "Contacted" -> GoldMetallic.copy(alpha = 0.25f) to GoldDark
          "Confirmed" -> Emerald700.copy(alpha = 0.15f) to Emerald700
          "Completed" -> StockGreen.copy(alpha = 0.15f) to StockGreen
          "Cancelled" -> StockRed.copy(alpha = 0.15f) to StockRed
          else -> Color.LightGray to Color.DarkGray
        }

        ExposedDropdownMenuBox(
          expanded = expandedStatusMenu,
          onExpandedChange = { expandedStatusMenu = !expandedStatusMenu }
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeBg,
            modifier = Modifier.menuAnchor()
          ) {
            Text(
              text = "${enquiry.status} ▾",
              color = badgeFg,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          ExposedDropdownMenu(
            expanded = expandedStatusMenu,
            onDismissRequest = { expandedStatusMenu = false }
          ) {
            listOf("New", "Contacted", "Confirmed", "Completed", "Cancelled").forEach { status ->
              DropdownMenuItem(
                text = { Text(status) },
                onClick = {
                  onUpdateStatus(status)
                  expandedStatusMenu = false
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Item: ${enquiry.productName} (${enquiry.productCode})",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "Qty: ${enquiry.quantity} pcs @ ₹${enquiry.historicalPrice.toInt()}/pc | Total: ₹${enquiry.totalAmount.toInt()}",
        fontSize = 12.sp,
        color = Emerald700,
        fontWeight = FontWeight.Bold
      )

      if (enquiry.address.isNotEmpty()) {
        Text(text = "City: ${enquiry.address}", fontSize = 11.sp, color = Color.Gray)
      }
      if (enquiry.message.isNotEmpty()) {
        Text(text = "Note: ${enquiry.message}", fontSize = 11.sp, color = Color.DarkGray)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onCall,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Emerald700)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Call", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onWhatsApp,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldShimmer)
          Spacer(modifier = Modifier.width(4.dp))
          Text("WhatsApp", fontSize = 11.sp, color = GoldShimmer, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
