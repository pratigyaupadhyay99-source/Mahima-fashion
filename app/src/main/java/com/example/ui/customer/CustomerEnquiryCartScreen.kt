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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.SandParchment

@Composable
fun CustomerEnquiryCartScreen(
  viewModel: FashionViewModel,
  onNavigateToCatalog: () -> Unit
) {
  val context = LocalContext.current
  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()

  var customerName by remember { mutableStateOf("") }
  var businessName by remember { mutableStateOf("") }
  var mobileNumber by remember { mutableStateOf("") }
  var cityAddress by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  val totalWholesaleAmount = cartItems.sumOf { it.product.wholesalePrice * it.quantity }
  val totalPieces = cartItems.sumOf { it.quantity }

  if (cartItems.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.ShoppingBag,
          contentDescription = "Empty Cart",
          tint = GoldDark,
          modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Your Wholesale Enquiry Cart is Empty",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Emerald900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Browse our catalog and add wholesale lots of abayas, dress materials and suits.",
          fontSize = 12.sp,
          color = Color.Gray,
          modifier = Modifier.padding(horizontal = 16.dp),
          lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = onNavigateToCatalog,
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Browse Catalog", color = GoldShimmer, fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    Text(
      text = "Wholesale Enquiry List (${cartItems.size} items)",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = Emerald900
    )
    Text(
      text = "Review your bulk selection and submit for dealer pricing quotation.",
      fontSize = 11.sp,
      color = Color.Gray
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Cart Items
    cartItems.forEach { item ->
      val p = item.product
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Product Thumbnail
          Image(
            painter = painterResource(
              if (p.thumbnail == "ic_mahima_logo") R.drawable.ic_mahima_logo else R.drawable.banner_fashion_hero
            ),
            contentDescription = p.name,
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
          )

          Spacer(modifier = Modifier.width(10.dp))

          // Product Info
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = p.name,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "SKU: ${p.productCode} • ₹${p.wholesalePrice.toInt()}/pc",
              fontSize = 11.sp,
              color = Emerald700,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Item Total: ₹${(p.wholesalePrice * item.quantity).toInt()}",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }

          // Quantity Adjuster
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(SandParchment, shape = RoundedCornerShape(6.dp))
              .padding(2.dp)
          ) {
            IconButton(
              onClick = {
                viewModel.updateCartItemQuantity(p.id, item.quantity - 1)
              },
              modifier = Modifier.size(26.dp)
            ) {
              Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
            }

            Text(
              text = "${item.quantity}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(
              onClick = {
                viewModel.updateCartItemQuantity(p.id, item.quantity + 1)
              },
              modifier = Modifier.size(26.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
            }
          }

          IconButton(
            onClick = { viewModel.updateCartItemQuantity(p.id, 0) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Summary Card
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = SandParchment,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Total Lot Pieces", fontSize = 12.sp, color = Color.Gray)
          Text("$totalPieces pcs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Est. Wholesale Subtotal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
          Text("₹${totalWholesaleAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Emerald700)
        }
        Text(
          text = "* Final invoice will include applicable GST & dispatch logistics.",
          fontSize = 10.sp,
          color = Color.Gray,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Customer & Shop Info Form
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Wholesale Buyer Details",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Emerald900
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Contact Person Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = businessName,
          onValueChange = { businessName = it },
          label = { Text("Shop / Boutique / Business Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = mobileNumber,
          onValueChange = { mobileNumber = it },
          label = { Text("WhatsApp / Mobile Number *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = cityAddress,
          onValueChange = { cityAddress = it },
          label = { Text("Delivery City / Full Address") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes / Color Assortment Requests") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit to DB Button
        Button(
          onClick = {
            if (customerName.isBlank() || mobileNumber.isBlank() || businessName.isBlank()) {
              viewModel.showMessage("Please fill your Name, Shop Name, and Mobile number.")
              return@Button
            }
            viewModel.submitBulkEnquiry(
              customerName = customerName,
              businessName = businessName,
              mobile = mobileNumber,
              address = cityAddress,
              notes = notes,
              onSuccess = {
                viewModel.navigateTo(ScreenDestination.CustomerHome)
              }
            )
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GoldShimmer)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Submit Wholesale Enquiry", color = GoldShimmer, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Send via WhatsApp Button
        OutlinedButton(
          onClick = {
            val itemsSummary = cartItems.joinToString("\n") {
              "- ${it.product.name} (${it.product.productCode}): ${it.quantity} pcs @ ₹${it.product.wholesalePrice.toInt()}"
            }
            val waMsg = "Hello Mahima Fashion, I want a wholesale order quotation:\n" +
              "Business: $businessName\n" +
              "Contact: $customerName ($mobileNumber)\n" +
              "City: $cityAddress\n\n" +
              "Items:\n$itemsSummary\n\n" +
              "Total: $totalPieces pcs | Est. ₹${totalWholesaleAmount.toInt()}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://api.whatsapp.com/send?phone=919327607195&text=${Uri.encode(waMsg)}")
            }
            context.startActivity(intent)
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700)
        ) {
          Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Emerald700)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Send Directly on WhatsApp", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}
