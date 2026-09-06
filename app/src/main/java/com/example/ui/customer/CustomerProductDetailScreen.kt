package com.example.ui.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.OrderEnquiryEntity
import com.example.data.model.ProductEntity
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.BadgeTag
import com.example.ui.theme.BadgeNewArrival
import com.example.ui.theme.BadgeSale
import com.example.ui.theme.BadgeTrending
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
fun CustomerProductDetailScreen(
  productId: Long,
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val products by viewModel.products.collectAsStateWithLifecycle()
  val product = products.firstOrNull { it.id == productId }

  if (product == null) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Product not found", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onBack) { Text("Back to Catalog") }
      }
    }
    return
  }

  // State
  var selectedImageRes by remember {
    mutableIntStateOf(
      if (product.thumbnail == "ic_mahima_logo") R.drawable.ic_mahima_logo else R.drawable.banner_fashion_hero
    )
  }
  var showZoomModal by remember { mutableStateOf(false) }
  var quantity by remember { mutableIntStateOf(product.minimumOrderQuantity) }
  var showDirectEnquiryDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(scrollState)
        .padding(bottom = 90.dp)
    ) {
      // 1. Image Gallery with Main Large Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .background(SandParchment)
      ) {
        Image(
          painter = painterResource(id = selectedImageRes),
          contentDescription = product.name,
          modifier = Modifier
            .fillMaxSize()
            .clickable { showZoomModal = true },
          contentScale = ContentScale.Crop
        )

        // Back button overlay
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(12.dp)
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(18.dp))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        // Zoom Indicator
        IconButton(
          onClick = { showZoomModal = true },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(18.dp))
        ) {
          Icon(
            imageVector = Icons.Default.ZoomIn,
            contentDescription = "Zoom",
            tint = Color.White
          )
        }

        // Badges overlay
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (product.newArrival) BadgeTag("NEW ARRIVAL", BadgeNewArrival)
          if (product.trending) BadgeTag("TRENDING", BadgeTrending)
          if (product.discountPercentage > 0) BadgeTag("${product.discountPercentage.toInt()}% OFF", BadgeSale)
        }
      }

      // Thumbnail Strip (Multiple product images)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val availableImages = listOf(
          R.drawable.banner_fashion_hero to "Cloth Texture & Drape",
          R.drawable.ic_mahima_logo to "Brand Seal"
        )
        availableImages.forEach { (imgRes, label) ->
          val isSelected = selectedImageRes == imgRes
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GoldMetallic else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { selectedImageRes = imgRes }
          ) {
            Image(
              painter = painterResource(id = imgRes),
              contentDescription = label,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        }
      }

      // 2. Product Title & Pricing Block
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SKU: ${product.productCode}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = GoldDark
            )

            // Stock badge
            val (stockText, stockColor) = when {
              product.stock <= 0 -> "Out of Stock" to StockRed
              product.stock <= product.minimumStock -> "Low Stock: ${product.stock} pcs left" to StockOrange
              else -> "In Stock: ${product.stock} pcs available" to StockGreen
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = stockColor.copy(alpha = 0.15f)
            ) {
              Text(
                text = stockText,
                color = stockColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = product.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Wholesale & Retail Pricing Grid
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SandParchment,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Wholesale B2B Rate",
                  fontSize = 11.sp,
                  color = Emerald700,
                  fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.Bottom) {
                  Text(
                    text = "₹${product.wholesalePrice.toInt()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Emerald700
                  )
                  Text(
                    text = " / piece",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 2.dp)
                  )
                }
                if (product.originalPrice > product.wholesalePrice) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "MRP: ₹${product.originalPrice.toInt()}",
                      fontSize = 11.sp,
                      color = Color.Gray,
                      textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Save ₹${(product.originalPrice - product.wholesalePrice).toInt()} (${product.discountPercentage.toInt()}%)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = BadgeSale
                    )
                  }
                }
              }

              // MOQ Box
              Column(horizontalAlignment = Alignment.End) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GoldMetallic
                ) {
                  Text(
                    text = "MOQ: ${product.minimumOrderQuantity} PCS",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "+ 5% GST Extra",
                  fontSize = 9.sp,
                  color = Color.Gray
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Product Specifications
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Product Specifications",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
          )
          Spacer(modifier = Modifier.height(8.dp))
          HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(8.dp))

          SpecRow("Category", product.category)
          SpecRow("Collection", product.collection)
          SpecRow("Fabric Material", product.fabric)
          SpecRow("Color / Shade", product.color)
          SpecRow("Design / Work", product.design)
          SpecRow("Size / Dimensions", product.size)
          SpecRow("Minimum Order Qty", "${product.minimumOrderQuantity} Pieces")

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Product Description",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = product.description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Quantity Selector (Enforcing MOQ)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Wholesale Lot Quantity",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald900
            )
            Text(
              text = "Total: ₹${(product.wholesalePrice * quantity).toInt()}",
              fontSize = 12.sp,
              color = Emerald700,
              fontWeight = FontWeight.SemiBold
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(SandParchment, shape = RoundedCornerShape(8.dp))
              .padding(4.dp)
          ) {
            IconButton(
              onClick = {
                if (quantity > product.minimumOrderQuantity) {
                  quantity -= 1
                } else {
                  viewModel.showMessage("Minimum order quantity is ${product.minimumOrderQuantity} pieces.")
                }
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Emerald900)
            }

            Text(
              text = "$quantity pcs",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp)
            )

            IconButton(
              onClick = { quantity += 1 },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Emerald900)
            }
          }
        }
      }
    }

    // Fixed Bottom Action Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 10.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // WhatsApp Direct Enquiry
        OutlinedButton(
          onClick = {
            val message = "Hello Mahima Fashion, I want to inquire about wholesale order for:\n" +
              "Product: ${product.name}\n" +
              "SKU: ${product.productCode}\n" +
              "Quantity: $quantity pcs\n" +
              "Rate: ₹${product.wholesalePrice.toInt()}/pc\n" +
              "Est. Total: ₹${(product.wholesalePrice * quantity).toInt()}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://api.whatsapp.com/send?phone=919327607195&text=${Uri.encode(message)}")
            }
            context.startActivity(intent)
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700)
        ) {
          Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "WhatsApp",
            tint = Emerald700,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Add to Enquiry / Cart
        Button(
          onClick = {
            viewModel.addToEnquiryCart(product, quantity)
          },
          modifier = Modifier.weight(1.3f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Icon(
            imageVector = Icons.Default.AddShoppingCart,
            contentDescription = "Add",
            tint = GoldShimmer,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Add to Cart ($quantity)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldShimmer)
        }
      }
    }

    // Zoom Image Dialog
    if (showZoomModal) {
      Dialog(onDismissRequest = { showZoomModal = false }) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
          Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Image(
              painter = painterResource(id = selectedImageRes),
              contentDescription = "Zoomed Product Image",
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
              contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showZoomModal = false },
              colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic)
            ) {
              Text("Close", color = Emerald900, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun SpecRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      color = Color.Gray
    )
    Text(
      text = value,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = Emerald900
    )
  }
}
