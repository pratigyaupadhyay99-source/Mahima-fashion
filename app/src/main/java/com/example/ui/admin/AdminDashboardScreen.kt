package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProductEntity
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
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
fun AdminDashboardScreen(
  viewModel: FashionViewModel
) {
  val currentUser by viewModel.currentAdminUser.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()
  val enquiries by viewModel.enquiries.collectAsStateWithLifecycle()
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()
  val expenses by viewModel.expenses.collectAsStateWithLifecycle()

  // Computed metrics
  val totalStockUnits = products.sumOf { it.stock }
  val lowStockProducts = products.filter { it.stock <= it.minimumStock }
  val pendingEnquiries = enquiries.filter { it.status == "New" || it.status == "Contacted" }
  val totalSalesRevenue = invoices.sumOf { it.totalAmount }
  val totalExpensesAmount = expenses.sumOf { it.amount }
  val estimatedProfit = totalSalesRevenue - totalExpensesAmount

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Admin Top Banner
    item {
      Surface(
        color = Emerald900,
        shadowElevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "MAHIMA FASHION B2B ADMIN",
                color = GoldLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "Logged in: ${currentUser?.name ?: "Admin"} (${currentUser?.role ?: "OWNER"})",
                color = GoldShimmer,
                fontSize = 11.sp
              )
            }

            Row {
              // View Customer Shop
              IconButton(
                onClick = { viewModel.navigateTo(ScreenDestination.CustomerHome) }
              ) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = "Customer Shop",
                  tint = GoldLight
                )
              }
              // Logout
              IconButton(
                onClick = { viewModel.logoutAdmin() }
              ) {
                Icon(
                  imageVector = Icons.Default.Logout,
                  contentDescription = "Logout",
                  tint = GoldLight
                )
              }
            }
          }
        }
      }
    }

    // 2. Low Stock Warning Bar
    if (lowStockProducts.isNotEmpty()) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { viewModel.navigateTo(ScreenDestination.AdminInventory) },
          shape = RoundedCornerShape(8.dp),
          color = StockOrange.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = StockOrange,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Low Stock Alert: ${lowStockProducts.size} Products",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = StockOrange
              )
              Text(
                text = "Replenish inventory to avoid wholesale dispatch delays.",
                fontSize = 10.sp,
                color = Color.DarkGray
              )
            }
            Text(
              text = "Restock >",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = StockOrange
            )
          }
        }
      }
    }

    // 3. Key Financial & Operational KPI Cards
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(
          text = "Business Overview & Revenue",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = Emerald900
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiCard(
            title = "Total Sales",
            value = "₹${totalSalesRevenue.toInt()}",
            subtitle = "${invoices.size} Invoices",
            color = Emerald700,
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "Pending Enquiries",
            value = "${pendingEnquiries.size}",
            subtitle = "Needs follow up",
            color = if (pendingEnquiries.isNotEmpty()) StockOrange else StockGreen,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiCard(
            title = "Total Products",
            value = "${products.size}",
            subtitle = "$totalStockUnits Units in Stock",
            color = Emerald800,
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "Net Est. Profit",
            value = "₹${estimatedProfit.toInt()}",
            subtitle = "Expenses: ₹${totalExpensesAmount.toInt()}",
            color = if (estimatedProfit >= 0) StockGreen else StockRed,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. Quick Module Navigation
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
          text = "Management Modules",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = Emerald900
        )
        Spacer(modifier = Modifier.height(8.dp))

        val menuItems = listOf(
          AdminMenuItem("Products & Catalog", "Add, edit, pricing, discounts, SKU", Icons.Default.Checkroom) {
            viewModel.navigateTo(ScreenDestination.AdminProducts)
          },
          AdminMenuItem("Categories", "Manage cloth classifications", Icons.Default.Category) {
            viewModel.navigateTo(ScreenDestination.AdminCategories)
          },
          AdminMenuItem("Wholesale Enquiries", "Boutique quotations (${pendingEnquiries.size} active)", Icons.Default.ShoppingBag) {
            viewModel.navigateTo(ScreenDestination.AdminEnquiries)
          },
          AdminMenuItem("Inventory & Stock", "Stock in, stock out, adjustments", Icons.Default.Inventory) {
            viewModel.navigateTo(ScreenDestination.AdminInventory)
          },
          AdminMenuItem("Sales & GST Invoicing", "Generate bills preserving historical price", Icons.Default.ReceiptLong) {
            viewModel.navigateTo(ScreenDestination.AdminBilling)
          },
          AdminMenuItem("Customers & Suppliers", "Boutiques, retailers & textile mills", Icons.Default.Group) {
            viewModel.navigateTo(ScreenDestination.AdminCustomersSuppliers)
          },
          AdminMenuItem("Finance & Expenses", "Record payments and shop expenses", Icons.Default.MonetizationOn) {
            viewModel.navigateTo(ScreenDestination.AdminFinance)
          },
          AdminMenuItem("Reports & Analytics", "Sales, profit/loss, inventory turnover", Icons.Default.Assessment) {
            viewModel.navigateTo(ScreenDestination.AdminReports)
          },
          AdminMenuItem("Staff & RBAC Permissions", "Roles, access privileges, users", Icons.Default.Security) {
            viewModel.navigateTo(ScreenDestination.AdminStaffRoles)
          },
          AdminMenuItem("Security Audit Logs", "Chronological system activity logs", Icons.Default.History) {
            viewModel.navigateTo(ScreenDestination.AdminAuditLogs)
          },
          AdminMenuItem("Business Settings", "Profile, GSTIN, invoice prefix & password", Icons.Default.Settings) {
            viewModel.navigateTo(ScreenDestination.AdminSettings)
          }
        )

        menuItems.forEach { item ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable { item.onClick() },
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
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Emerald700.copy(alpha = 0.12f),
                modifier = Modifier.size(38.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(imageVector = item.icon, contentDescription = item.title, tint = Emerald700, modifier = Modifier.size(20.dp))
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                Text(text = item.subtitle, fontSize = 10.sp, color = Color.Gray)
              }
              Text(text = ">", fontSize = 14.sp, color = GoldDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

data class AdminMenuItem(
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val onClick: () -> Unit
)

@Composable
fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, fontSize = 10.sp, color = Color.DarkGray)
    }
  }
}
