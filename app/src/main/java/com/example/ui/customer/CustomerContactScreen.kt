package com.example.ui.customer

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer

@Composable
fun CustomerContactScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 60.dp)
  ) {
    // Header
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
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = GoldLight
          )
        }
        Text(
          text = "Wholesale Office & Contact",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Column(modifier = Modifier.padding(16.dp)) {
      // Business Profile Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              painter = painterResource(id = R.drawable.ic_mahima_logo),
              contentDescription = "Logo",
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "MAHIMA FASHION",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald900
              )
              Text(
                text = "Wholesale Muslim & Modest Clothing",
                fontSize = 11.sp,
                color = GoldDark,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(14.dp))

          ContactInfoRow(
            icon = Icons.Default.LocationOn,
            label = "Shop & Wholesale Depot Address",
            value = "2070, New Pashupati Textile Market, Opp. Shyam Market, Moti Begam Wadi, Ring Road, Surat, Gujarat"
          )

          Spacer(modifier = Modifier.height(10.dp))

          ContactInfoRow(
            icon = Icons.Default.Phone,
            label = "Mobile Number (Calling)",
            value = "+91 9327607195"
          )

          Spacer(modifier = Modifier.height(10.dp))

          ContactInfoRow(
            icon = Icons.Default.Chat,
            label = "Message Number (WhatsApp)",
            value = "+91 9327607195"
          )

          Spacer(modifier = Modifier.height(10.dp))

          ContactInfoRow(
            icon = Icons.Default.Email,
            label = "B2B Email",
            value = "wholesale@mahimafashion.com"
          )

          Spacer(modifier = Modifier.height(10.dp))

          ContactInfoRow(
            icon = Icons.Default.Business,
            label = "GSTIN",
            value = "24AAACM1234F1Z8 (Verified Registered Wholesaler)"
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Direct Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            val intent = Intent(Intent.ACTION_DIAL).apply {
              data = Uri.parse("tel:+919327607195")
            }
            context.startActivity(intent)
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = GoldShimmer, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Depot", color = GoldShimmer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Button(
          onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://api.whatsapp.com/send?phone=919327607195&text=Hello%20Mahima%20Fashion,%20I%20am%20a%20retailer/boutique%20owner%20and%20want%20to%20place%20a%20wholesale%20order.")
            }
            context.startActivity(intent)
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = Emerald900, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("WhatsApp", color = Emerald900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Wholesale Policies Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald800.copy(alpha = 0.06f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Wholesale Terms & Dispatch Guidelines",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Emerald900
          )
          Spacer(modifier = Modifier.height(8.dp))
          PolicyBullet("Minimum Order Quantity (MOQ) applies per design lot (typically 5 to 12 pieces).")
          PolicyBullet("Direct parcel dispatch to all states and tier-1/2/3 cities across India.")
          PolicyBullet("Official GST invoices issued for all wholesale dispatches.")
          PolicyBullet("Transport via V-Trans, SafeExpress, Trackon, and Delhivery Cargo.")
        }
      }
    }
  }
}

@Composable
fun ContactInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = Emerald700,
      modifier = Modifier
        .size(18.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = label, fontSize = 11.sp, color = Color.Gray)
      Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Emerald900)
    }
  }
}

@Composable
fun PolicyBullet(text: String) {
  Row(
    modifier = Modifier.padding(vertical = 3.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(text = "• ", fontWeight = FontWeight.Bold, color = Emerald700)
    Text(text = text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
  }
}
