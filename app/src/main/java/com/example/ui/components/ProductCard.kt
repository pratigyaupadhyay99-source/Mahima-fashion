package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.ProductEntity
import com.example.ui.theme.BadgeNewArrival
import com.example.ui.theme.BadgeSale
import com.example.ui.theme.BadgeTrending
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.SandParchment
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductCard(
  product: ProductEntity,
  onClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Product Image Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(0.95f)
          .background(SandParchment)
      ) {
        val resId = when (product.thumbnail) {
          "banner_fashion_hero" -> R.drawable.banner_fashion_hero
          "ic_mahima_logo" -> R.drawable.ic_mahima_logo
          else -> R.drawable.banner_fashion_hero
        }

        Image(
          painter = painterResource(id = resId),
          contentDescription = product.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Badges container (Top Left)
        Column(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (product.newArrival) {
            BadgeTag("NEW", BadgeNewArrival)
          }
          if (product.trending) {
            BadgeTag("TRENDING", BadgeTrending)
          }
          if (product.discountPercentage > 0) {
            BadgeTag("${product.discountPercentage.toInt()}% OFF", BadgeSale)
          }
        }

        // Stock Status Chip (Bottom Left)
        val (stockText, stockColor) = when {
          product.stock <= 0 -> "Out of Stock" to StockRed
          product.stock <= product.minimumStock -> "Low Stock: ${product.stock}" to StockOrange
          else -> "In Stock: ${product.stock}" to StockGreen
        }

        Surface(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(6.dp),
          shape = RoundedCornerShape(4.dp),
          color = Color.Black.copy(alpha = 0.65f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(stockColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stockText,
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Add to enquiry button (Bottom Right)
        IconButton(
          onClick = onAddToCart,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(6.dp)
            .size(36.dp)
            .background(Emerald700, shape = RoundedCornerShape(8.dp))
        ) {
          Icon(
            imageVector = Icons.Default.AddShoppingCart,
            contentDescription = "Add to wholesale enquiry",
            tint = GoldShimmer,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Details Content
      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = product.productCode,
          style = MaterialTheme.typography.labelSmall,
          color = GoldDark,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = product.name,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = "${product.fabric} • ${product.color}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Wholesale Price",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = Emerald700,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "₹${product.wholesalePrice.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Emerald700
              )
              if (product.originalPrice > product.wholesalePrice) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "₹${product.originalPrice.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textDecoration = TextDecoration.LineThrough,
                  fontSize = 11.sp
                )
              }
            }
          }

          // MOQ Tag
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = GoldShimmer
          ) {
            Text(
              text = "MOQ: ${product.minimumOrderQuantity}",
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald700
            )
          }
        }
      }
    }
  }
}

@Composable
fun BadgeTag(text: String, backgroundColor: Color) {
  Surface(
    shape = RoundedCornerShape(4.dp),
    color = backgroundColor,
    shadowElevation = 1.dp
  ) {
    Text(
      text = text,
      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
      color = Color.White,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}
