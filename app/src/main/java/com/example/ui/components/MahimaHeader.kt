package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer

@Composable
fun MahimaHeader(
  cartCount: Int,
  isAdminLoggedIn: Boolean,
  onOpenCart: () -> Unit,
  onOpenAdmin: () -> Unit,
  onBack: (() -> Unit)? = null,
  title: String? = null,
  subtitle: String? = null
) {
  Surface(
    color = Emerald900,
    shadowElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = GoldLight
          )
        }
      } else {
        // Logo
        Image(
          painter = painterResource(id = R.drawable.ic_mahima_logo),
          contentDescription = "Mahima Fashion Logo",
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title ?: "MAHIMA FASHION",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = subtitle ?: "WHOLESALE MODEST & DRESS MATERIALS",
          color = GoldShimmer.copy(alpha = 0.8f),
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 0.8.sp
        )
      }

      // Action Buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Wholesale Cart / Enquiry List Button
        IconButton(onClick = onOpenCart) {
          BadgedBox(
            badge = {
              if (cartCount > 0) {
                Badge(
                  containerColor = GoldMetallic,
                  contentColor = Emerald900
                ) {
                  Text(
                    text = cartCount.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = "Wholesale Enquiry Cart",
              tint = GoldLight
            )
          }
        }

        // Admin Access Button
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onOpenAdmin() },
          color = if (isAdminLoggedIn) GoldMetallic.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
          shape = RoundedCornerShape(20.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (isAdminLoggedIn) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
              contentDescription = "Admin Portal",
              tint = if (isAdminLoggedIn) GoldLight else Color.White.copy(alpha = 0.85f),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isAdminLoggedIn) "Admin" else "Staff",
              color = if (isAdminLoggedIn) GoldLight else Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}
