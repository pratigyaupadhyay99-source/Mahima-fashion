package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
import com.example.ui.admin.AdminAuditLogsScreen
import com.example.ui.admin.AdminAuthScreen
import com.example.ui.admin.AdminBillingScreen
import com.example.ui.admin.AdminCategoriesScreen
import com.example.ui.admin.AdminCustomersSuppliersScreen
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminEnquiriesScreen
import com.example.ui.admin.AdminFinanceScreen
import com.example.ui.admin.AdminInventoryScreen
import com.example.ui.admin.AdminProductsScreen
import com.example.ui.admin.AdminReportsScreen
import com.example.ui.admin.AdminSettingsScreen
import com.example.ui.admin.AdminStaffRolesScreen
import com.example.ui.components.MahimaHeader
import com.example.ui.customer.CustomerCatalogScreen
import com.example.ui.customer.CustomerContactScreen
import com.example.ui.customer.CustomerEnquiryCartScreen
import com.example.ui.customer.CustomerHomeScreen
import com.example.ui.customer.CustomerProductDetailScreen
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

  private val viewModel: FashionViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
        val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
        val currentAdminUser by viewModel.currentAdminUser.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        // Observe toast messages
        LaunchedEffect(Unit) {
          viewModel.userMessage.collectLatest { msg ->
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
          }
        }

        // Back Handler handling
        BackHandler(enabled = currentScreen !is ScreenDestination.CustomerHome) {
          when (currentScreen) {
            is ScreenDestination.CustomerProductDetail,
            is ScreenDestination.CustomerCatalog,
            is ScreenDestination.CustomerEnquiryCart,
            is ScreenDestination.CustomerContact,
            is ScreenDestination.AdminAuth -> {
              viewModel.navigateTo(ScreenDestination.CustomerHome)
            }
            is ScreenDestination.AdminDashboard -> {
              viewModel.navigateTo(ScreenDestination.CustomerHome)
            }
            is ScreenDestination.AdminProducts,
            is ScreenDestination.AdminCategories,
            is ScreenDestination.AdminEnquiries,
            is ScreenDestination.AdminInventory,
            is ScreenDestination.AdminBilling,
            is ScreenDestination.AdminCustomersSuppliers,
            is ScreenDestination.AdminFinance,
            is ScreenDestination.AdminReports,
            is ScreenDestination.AdminStaffRoles,
            is ScreenDestination.AdminAuditLogs,
            is ScreenDestination.AdminSettings -> {
              viewModel.navigateTo(ScreenDestination.AdminDashboard)
            }
            else -> {
              viewModel.navigateTo(ScreenDestination.CustomerHome)
            }
          }
        }

        val isCustomerMode = when (currentScreen) {
          is ScreenDestination.CustomerHome,
          is ScreenDestination.CustomerCatalog,
          is ScreenDestination.CustomerProductDetail,
          is ScreenDestination.CustomerEnquiryCart,
          is ScreenDestination.CustomerContact -> true
          else -> false
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          snackbarHost = { SnackbarHost(snackbarHostState) },
          topBar = {
            if (isCustomerMode && currentScreen !is ScreenDestination.CustomerProductDetail) {
              MahimaHeader(
                cartCount = cartItems.sumOf { it.quantity },
                isAdminLoggedIn = currentAdminUser != null,
                onOpenCart = { viewModel.navigateTo(ScreenDestination.CustomerEnquiryCart) },
                onOpenAdmin = {
                  if (currentAdminUser != null) {
                    viewModel.navigateTo(ScreenDestination.AdminDashboard)
                  } else {
                    viewModel.navigateTo(ScreenDestination.AdminAuth)
                  }
                },
                onBack = if (currentScreen !is ScreenDestination.CustomerHome) {
                  { viewModel.navigateTo(ScreenDestination.CustomerHome) }
                } else null
              )
            }
          },
          bottomBar = {
            if (isCustomerMode) {
              CustomerBottomBar(
                currentScreen = currentScreen,
                cartCount = cartItems.sumOf { it.quantity },
                onNavigate = { viewModel.navigateTo(it) }
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (val screen = currentScreen) {
              is ScreenDestination.CustomerHome -> {
                CustomerHomeScreen(
                  viewModel = viewModel,
                  onOpenProduct = { prodId ->
                    viewModel.navigateTo(ScreenDestination.CustomerProductDetail(prodId))
                  },
                  onViewAllCatalog = { category ->
                    category?.let { viewModel.selectedCategoryFilter.value = it }
                    viewModel.navigateTo(ScreenDestination.CustomerCatalog)
                  }
                )
              }

              is ScreenDestination.CustomerCatalog -> {
                CustomerCatalogScreen(
                  viewModel = viewModel,
                  onOpenProduct = { prodId ->
                    viewModel.navigateTo(ScreenDestination.CustomerProductDetail(prodId))
                  }
                )
              }

              is ScreenDestination.CustomerProductDetail -> {
                CustomerProductDetailScreen(
                  productId = screen.productId,
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.CustomerCatalog) }
                )
              }

              is ScreenDestination.CustomerEnquiryCart -> {
                CustomerEnquiryCartScreen(
                  viewModel = viewModel,
                  onNavigateToCatalog = { viewModel.navigateTo(ScreenDestination.CustomerCatalog) }
                )
              }

              is ScreenDestination.CustomerContact -> {
                CustomerContactScreen(
                  onBack = { viewModel.navigateTo(ScreenDestination.CustomerHome) }
                )
              }

              // Admin Flow
              is ScreenDestination.AdminAuth -> {
                AdminAuthScreen(
                  viewModel = viewModel,
                  onBackToShop = { viewModel.navigateTo(ScreenDestination.CustomerHome) }
                )
              }

              is ScreenDestination.AdminDashboard -> {
                AdminDashboardScreen(viewModel = viewModel)
              }

              is ScreenDestination.AdminProducts -> {
                AdminProductsScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminCategories -> {
                AdminCategoriesScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminEnquiries -> {
                AdminEnquiriesScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminInventory -> {
                AdminInventoryScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminBilling -> {
                AdminBillingScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminCustomersSuppliers -> {
                AdminCustomersSuppliersScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminFinance -> {
                AdminFinanceScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminReports -> {
                AdminReportsScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminStaffRoles -> {
                AdminStaffRolesScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminAuditLogs -> {
                AdminAuditLogsScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              is ScreenDestination.AdminSettings -> {
                AdminSettingsScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                )
              }

              else -> {
                CustomerHomeScreen(
                  viewModel = viewModel,
                  onOpenProduct = { prodId ->
                    viewModel.navigateTo(ScreenDestination.CustomerProductDetail(prodId))
                  },
                  onViewAllCatalog = { category ->
                    category?.let { viewModel.selectedCategoryFilter.value = it }
                    viewModel.navigateTo(ScreenDestination.CustomerCatalog)
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomerBottomBar(
  currentScreen: ScreenDestination,
  cartCount: Int,
  onNavigate: (ScreenDestination) -> Unit
) {
  NavigationBar(
    containerColor = Emerald900,
    contentColor = GoldLight
  ) {
    NavigationBarItem(
      selected = currentScreen is ScreenDestination.CustomerHome,
      onClick = { onNavigate(ScreenDestination.CustomerHome) },
      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
      label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Emerald900,
        selectedTextColor = GoldLight,
        indicatorColor = GoldMetallic,
        unselectedIconColor = GoldShimmer.copy(alpha = 0.6f),
        unselectedTextColor = GoldShimmer.copy(alpha = 0.6f)
      )
    )

    NavigationBarItem(
      selected = currentScreen is ScreenDestination.CustomerCatalog,
      onClick = { onNavigate(ScreenDestination.CustomerCatalog) },
      icon = { Icon(Icons.Default.Checkroom, contentDescription = "Catalog") },
      label = { Text("Catalog", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Emerald900,
        selectedTextColor = GoldLight,
        indicatorColor = GoldMetallic,
        unselectedIconColor = GoldShimmer.copy(alpha = 0.6f),
        unselectedTextColor = GoldShimmer.copy(alpha = 0.6f)
      )
    )

    NavigationBarItem(
      selected = currentScreen is ScreenDestination.CustomerEnquiryCart,
      onClick = { onNavigate(ScreenDestination.CustomerEnquiryCart) },
      icon = {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(
                containerColor = GoldMetallic,
                contentColor = Emerald900
              ) {
                Text(text = cartCount.toString(), fontWeight = FontWeight.Bold, fontSize = 9.sp)
              }
            }
          }
        ) {
          Icon(Icons.Default.ShoppingBag, contentDescription = "Enquiry Cart")
        }
      },
      label = { Text("Bulk Cart", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Emerald900,
        selectedTextColor = GoldLight,
        indicatorColor = GoldMetallic,
        unselectedIconColor = GoldShimmer.copy(alpha = 0.6f),
        unselectedTextColor = GoldShimmer.copy(alpha = 0.6f)
      )
    )

    NavigationBarItem(
      selected = currentScreen is ScreenDestination.CustomerContact,
      onClick = { onNavigate(ScreenDestination.CustomerContact) },
      icon = { Icon(Icons.Default.Business, contentDescription = "Wholesale Office") },
      label = { Text("Contact", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Emerald900,
        selectedTextColor = GoldLight,
        indicatorColor = GoldMetallic,
        unselectedIconColor = GoldShimmer.copy(alpha = 0.6f),
        unselectedTextColor = GoldShimmer.copy(alpha = 0.6f)
      )
    )
  }
}

