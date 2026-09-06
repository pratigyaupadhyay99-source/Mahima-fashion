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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed

@Composable
fun AdminReportsScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val products by viewModel.products.collectAsStateWithLifecycle()
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()
  val expenses by viewModel.expenses.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()

  val totalInventoryValue = products.sumOf { it.wholesalePrice * it.stock }
  val totalPurchaseCost = products.sumOf { it.purchasePrice * it.stock }
  val totalSalesRevenue = invoices.sumOf { it.totalAmount }
  val totalExpenses = expenses.sumOf { it.amount }
  val grossProfitEstimate = totalSalesRevenue - totalExpenses

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
          text = "Wholesale Reports & Analytics",
          color = GoldLight,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Inventory Valuation Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Inventory Asset Valuation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Units in Warehouse", fontSize = 12.sp, color = Color.Gray)
              Text("${products.sumOf { it.stock }} Pieces", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Wholesale Catalog Value", fontSize = 12.sp, color = Color.Gray)
              Text("₹${totalInventoryValue.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Emerald700)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Estimated Purchase Cost Value", fontSize = 12.sp, color = Color.Gray)
              Text("₹${totalPurchaseCost.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      // 2. Category Distribution
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Stock Distribution by Category", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Spacer(modifier = Modifier.height(12.dp))

            val totalStock = products.sumOf { it.stock }.coerceAtLeast(1)
            categories.forEach { cat ->
              val catStock = products.filter { it.category.equals(cat.name, ignoreCase = true) }.sumOf { it.stock }
              if (catStock > 0) {
                val ratio = (catStock.toFloat() / totalStock).coerceIn(0f, 1f)
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text("$catStock pcs (${(ratio * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald700)
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp)),
                    color = Emerald700,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                  )
                }
              }
            }
          }
        }
      }

      // 3. Profit / Loss Performance Summary
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Operational Profitability", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Invoiced Sales", fontSize = 12.sp)
              Text("₹${totalSalesRevenue.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald700)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Operational Expenses", fontSize = 12.sp)
              Text("₹${totalExpenses.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StockRed)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Net Profit / Surplus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
              Text(
                text = "₹${grossProfitEstimate.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (grossProfitEstimate >= 0) StockGreen else StockRed
              )
            }
          }
        }
      }
    }
  }
}
