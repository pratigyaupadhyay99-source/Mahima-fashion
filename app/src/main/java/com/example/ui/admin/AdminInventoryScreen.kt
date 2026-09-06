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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.InventoryLogEntity
import com.example.data.model.ProductEntity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminInventoryScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val products by viewModel.products.collectAsStateWithLifecycle()
  val inventoryLogs by viewModel.inventoryLogs.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Stock Overview, 1 = Stock Movements History
  var searchQuery by remember { mutableStateOf("") }
  var productToAdjust by remember { mutableStateOf<ProductEntity?>(null) }

  val filteredProducts = products.filter {
    searchQuery.isBlank() ||
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.productCode.contains(searchQuery, ignoreCase = true)
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
          text = "Inventory & Stock Control",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = Emerald700
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Stock Status", fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Movement Logs (${inventoryLogs.size})", fontWeight = FontWeight.Bold) }
      )
    }

    if (selectedTab == 0) {
      // Stock Status Tab
      Column(modifier = Modifier.padding(14.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search product name or SKU...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredProducts, key = { it.id }) { product ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = product.productCode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                  Text(text = product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  Text(text = "Min Alert: ${product.minimumStock} | MOQ: ${product.minimumOrderQuantity}", fontSize = 10.sp, color = Color.Gray)
                }

                val (statusText, statusColor) = when {
                  product.stock <= 0 -> "OUT OF STOCK" to StockRed
                  product.stock <= product.minimumStock -> "LOW (${product.stock})" to StockOrange
                  else -> "${product.stock} IN STOCK" to StockGreen
                }

                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = statusColor.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                  onClick = { productToAdjust = product },
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.height(32.dp)
                ) {
                  Text("Adjust", fontSize = 11.sp, color = GoldShimmer, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    } else {
      // Movement Logs Tab
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(inventoryLogs, key = { it.id }) { log ->
          val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
          val dateStr = dateFormat.format(Date(log.timestamp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val isPositive = log.changeAmount >= 0
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isPositive) StockGreen.copy(alpha = 0.15f) else StockRed.copy(alpha = 0.15f)
              ) {
                Text(
                  text = if (isPositive) "+${log.changeAmount}" else "${log.changeAmount}",
                  color = if (isPositive) StockGreen else StockRed,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(text = "${log.productName} (${log.sku})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Reason: ${log.reason} • By ${log.performedBy}", fontSize = 10.sp, color = Color.DarkGray)
                Text(text = "Stock: ${log.previousStock} → ${log.newStock} | $dateStr", fontSize = 10.sp, color = Color.Gray)
              }
            }
          }
        }
      }
    }

    // Stock Adjust Dialog
    if (productToAdjust != null) {
      StockAdjustDialog(
        product = productToAdjust!!,
        onDismiss = { productToAdjust = null },
        onConfirm = { delta, reason ->
          viewModel.adjustStock(productToAdjust!!.id, delta, reason)
          productToAdjust = null
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustDialog(
  product: ProductEntity,
  onDismiss: () -> Unit,
  onConfirm: (delta: Int, reason: String) -> Unit
) {
  var isStockIn by remember { mutableStateOf(true) }
  var quantityText by remember { mutableStateOf("10") }
  var reason by remember { mutableStateOf("Purchase from Textile Mill") }

  val reasons = listOf(
    "Purchase from Textile Mill",
    "Return from Customer",
    "Wholesale Dispatch",
    "Sample Given to Boutique",
    "Damaged / Fabric Defect",
    "Physical Count Correction"
  )
  var reasonExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Adjust Stock: ${product.name}") },
    text = {
      Column {
        Text("Current in stock: ${product.stock} pieces", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = { isStockIn = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isStockIn) StockGreen else Color.LightGray
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text("Stock In (+)", color = Color.White)
          }
          Button(
            onClick = { isStockIn = false },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (!isStockIn) StockRed else Color.LightGray
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text("Stock Out (-)", color = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = quantityText,
          onValueChange = { quantityText = it },
          label = { Text("Quantity to adjust *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
          expanded = reasonExpanded,
          onExpandedChange = { reasonExpanded = !reasonExpanded }
        ) {
          OutlinedTextField(
            value = reason,
            onValueChange = {},
            readOnly = true,
            label = { Text("Reason for Adjustment") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reasonExpanded) },
            modifier = Modifier.menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = reasonExpanded,
            onDismissRequest = { reasonExpanded = false }
          ) {
            reasons.forEach { r ->
              DropdownMenuItem(
                text = { Text(r) },
                onClick = {
                  reason = r
                  reasonExpanded = false
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val qty = quantityText.toIntOrNull() ?: 0
          if (qty > 0) {
            val delta = if (isStockIn) qty else -qty
            onConfirm(delta, reason)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
      ) {
        Text("Confirm Adjustment", color = GoldShimmer)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
