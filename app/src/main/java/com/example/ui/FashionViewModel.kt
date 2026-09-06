package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.MahimaDatabase
import com.example.data.model.AdminUserEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.InventoryLogEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.OrderEnquiryEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SupplierEntity
import com.example.data.repository.FashionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EnquiryCartItem(
  val product: ProductEntity,
  var quantity: Int
)

sealed class ScreenDestination {
  object CustomerHome : ScreenDestination()
  object CustomerCatalog : ScreenDestination()
  data class CustomerProductDetail(val productId: Long) : ScreenDestination()
  object CustomerEnquiryCart : ScreenDestination()
  object CustomerContact : ScreenDestination()

  object AdminAuth : ScreenDestination()
  object AdminDashboard : ScreenDestination()
  object AdminProducts : ScreenDestination()
  data class AdminAddEditProduct(val productId: Long? = null) : ScreenDestination()
  object AdminCategories : ScreenDestination()
  object AdminEnquiries : ScreenDestination()
  object AdminInventory : ScreenDestination()
  object AdminBilling : ScreenDestination()
  object AdminCustomersSuppliers : ScreenDestination()
  object AdminFinance : ScreenDestination()
  object AdminReports : ScreenDestination()
  object AdminStaffRoles : ScreenDestination()
  object AdminAuditLogs : ScreenDestination()
  object AdminSettings : ScreenDestination()
}

class FashionViewModel(application: Application) : AndroidViewModel(application) {

  private val database = MahimaDatabase.getDatabase(application, viewModelScope)
  val repository = FashionRepository(database.fashionDao())

  // Navigation
  private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.CustomerHome)
  val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

  // Authentication & Session
  private val _currentAdminUser = MutableStateFlow<AdminUserEntity?>(null)
  val currentAdminUser: StateFlow<AdminUserEntity?> = _currentAdminUser.asStateFlow()

  private val _hasOwnerAccount = MutableStateFlow<Boolean?>(null)
  val hasOwnerAccount: StateFlow<Boolean?> = _hasOwnerAccount.asStateFlow()

  // Toast / Message Notifications
  private val _userMessage = MutableSharedFlow<String>()
  val userMessage = _userMessage.asSharedFlow()

  // Data Flows
  val products: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val trendingProducts: StateFlow<List<ProductEntity>> = repository.trendingProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val newArrivals: StateFlow<List<ProductEntity>> = repository.newArrivals
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val featuredProducts: StateFlow<List<ProductEntity>> = repository.featuredProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val discountedProducts: StateFlow<List<ProductEntity>> = repository.discountedProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val enquiries: StateFlow<List<OrderEnquiryEntity>> = repository.allEnquiries
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inventoryLogs: StateFlow<List<InventoryLogEntity>> = repository.allInventoryLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val staffUsers: StateFlow<List<AdminUserEntity>> = repository.allStaffUsers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val settings: StateFlow<List<AppSettingEntity>> = repository.allSettings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Customer Filtering & Search
  val searchQuery = MutableStateFlow("")
  val selectedCategoryFilter = MutableStateFlow("All")
  val selectedBadgeFilter = MutableStateFlow("All") // "All", "Trending", "New Arrival", "Featured", "Discount"

  // Filtered Products for Catalog
  val filteredProducts: StateFlow<List<ProductEntity>> = combine(
    products,
    searchQuery,
    selectedCategoryFilter,
    selectedBadgeFilter
  ) { list, query, category, badge ->
    list.filter { product ->
      val matchesQuery = query.isBlank() ||
        product.name.contains(query, ignoreCase = true) ||
        product.productCode.contains(query, ignoreCase = true) ||
        product.fabric.contains(query, ignoreCase = true) ||
        product.color.contains(query, ignoreCase = true) ||
        product.category.contains(query, ignoreCase = true)

      val matchesCategory = category == "All" || product.category.equals(category, ignoreCase = true)

      val matchesBadge = when (badge) {
        "Trending" -> product.trending
        "New Arrival" -> product.newArrival
        "Featured" -> product.featured
        "Discount" -> product.discountPercentage > 0
        else -> true
      }

      matchesQuery && matchesCategory && matchesBadge
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Customer Enquiry Cart
  private val _cartItems = MutableStateFlow<List<EnquiryCartItem>>(emptyList())
  val cartItems: StateFlow<List<EnquiryCartItem>> = _cartItems.asStateFlow()

  init {
    checkOwnerSetupStatus()
  }

  fun checkOwnerSetupStatus() {
    viewModelScope.launch {
      val hasOwner = repository.hasOwnerAccount()
      _hasOwnerAccount.value = hasOwner
    }
  }

  fun navigateTo(destination: ScreenDestination) {
    _currentScreen.value = destination
  }

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _userMessage.emit(msg)
    }
  }

  // --- CART / WHOLESALE ENQUIRY ACTIONS ---
  fun addToEnquiryCart(product: ProductEntity, quantity: Int = product.minimumOrderQuantity) {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst { it.product.id == product.id }
    if (existingIndex >= 0) {
      val item = current[existingIndex]
      current[existingIndex] = item.copy(quantity = item.quantity + quantity)
    } else {
      current.add(EnquiryCartItem(product, quantity.coerceAtLeast(product.minimumOrderQuantity)))
    }
    _cartItems.value = current
    showMessage("Added ${product.name} to wholesale enquiry cart.")
  }

  fun updateCartItemQuantity(productId: Long, newQuantity: Int) {
    val current = _cartItems.value.toMutableList()
    val index = current.indexOfFirst { it.product.id == productId }
    if (index >= 0) {
      if (newQuantity <= 0) {
        current.removeAt(index)
      } else {
        current[index] = current[index].copy(quantity = newQuantity)
      }
      _cartItems.value = current
    }
  }

  fun clearCart() {
    _cartItems.value = emptyList()
  }

  fun submitBulkEnquiry(
    customerName: String,
    businessName: String,
    mobile: String,
    address: String,
    notes: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      val items = _cartItems.value
      if (items.isEmpty()) {
        showMessage("Enquiry cart is empty.")
        return@launch
      }
      for (item in items) {
        val enquiry = OrderEnquiryEntity(
          customerName = customerName.trim(),
          businessName = businessName.trim(),
          mobile = mobile.trim(),
          address = address.trim(),
          productId = item.product.id,
          productName = item.product.name,
          productCode = item.product.productCode,
          quantity = item.quantity,
          historicalPrice = item.product.wholesalePrice,
          totalAmount = item.product.wholesalePrice * item.quantity,
          message = notes.trim()
        )
        repository.submitCustomerEnquiry(enquiry)
      }
      clearCart()
      showMessage("Wholesale enquiry submitted successfully! Our team will contact you on WhatsApp/Phone.")
      onSuccess()
    }
  }

  // --- AUTHENTICATION ACTIONS ---
  fun registerOwner(
    name: String,
    emailOrMobile: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.registerOwner(name, emailOrMobile, password)
      result.fold(
        onSuccess = { owner ->
          _currentAdminUser.value = owner
          _hasOwnerAccount.value = true
          showMessage("Welcome to Mahima Fashion Admin, ${owner.name}!")
          _currentScreen.value = ScreenDestination.AdminDashboard
          onSuccess()
        },
        onFailure = { error ->
          onError(error.localizedMessage ?: "Registration failed")
        }
      )
    }
  }

  fun loginAdmin(
    emailOrMobile: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.loginAdmin(emailOrMobile, password)
      result.fold(
        onSuccess = { user ->
          _currentAdminUser.value = user
          showMessage("Logged in as ${user.name} (${user.role})")
          _currentScreen.value = ScreenDestination.AdminDashboard
          onSuccess()
        },
        onFailure = { error ->
          onError(error.localizedMessage ?: "Login failed")
        }
      )
    }
  }

  fun logoutAdmin() {
    _currentAdminUser.value = null
    showMessage("Logged out successfully.")
    _currentScreen.value = ScreenDestination.CustomerHome
  }

  // --- PRODUCT MANAGEMENT ACTIONS ---
  fun saveProduct(product: ProductEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val opName = user?.name ?: "Admin"
      val opRole = user?.role ?: "ADMIN"
      repository.saveProduct(product, opName, opRole)
      showMessage("Product '${product.name}' saved successfully to database.")
      onSuccess()
    }
  }

  fun deleteProduct(product: ProductEntity) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val opName = user?.name ?: "Admin"
      val opRole = user?.role ?: "ADMIN"
      repository.deleteProduct(product, opName, opRole)
      showMessage("Product '${product.name}' deleted.")
    }
  }

  fun adjustStock(productId: Long, delta: Int, reason: String) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val opName = user?.name ?: "Admin"
      val opRole = user?.role ?: "ADMIN"
      repository.adjustStock(productId, delta, reason, opName, opRole)
      showMessage("Stock adjusted successfully.")
    }
  }

  // --- ORDER STATUS UPDATES ---
  fun updateEnquiryStatus(enquiryId: Long, newStatus: String) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val opName = user?.name ?: "Admin"
      val opRole = user?.role ?: "ADMIN"
      repository.updateEnquiryStatus(enquiryId, newStatus, opName, opRole)
      showMessage("Enquiry status updated to '$newStatus'.")
    }
  }

  // --- BILLING / INVOICING ---
  fun createInvoice(invoice: InvoiceEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val opName = user?.name ?: "Admin"
      val opRole = user?.role ?: "ADMIN"
      repository.createInvoice(invoice, opName, opRole)
      showMessage("Invoice #${invoice.invoiceNumber} created and saved.")
      onSuccess()
    }
  }

  // --- PAYMENTS & EXPENSES ---
  fun recordPayment(payment: PaymentEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      repository.recordPayment(payment, user?.name ?: "Admin")
      showMessage("Payment of ₹${payment.amount} recorded.")
      onSuccess()
    }
  }

  fun recordExpense(expense: ExpenseEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      repository.recordExpense(expense, user?.name ?: "Admin")
      showMessage("Expense of ₹${expense.amount} recorded.")
      onSuccess()
    }
  }

  // --- STAFF & ROLES ---
  fun addStaff(
    name: String,
    emailOrMobile: String,
    password: String,
    role: String,
    permissions: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      val user = _currentAdminUser.value
      val result = repository.addStaff(
        name,
        emailOrMobile,
        password,
        role,
        permissions,
        user?.name ?: "Owner"
      )
      result.fold(
        onSuccess = {
          showMessage("Staff member ${it.name} created.")
          onSuccess()
        },
        onFailure = {
          onError(it.localizedMessage ?: "Failed to add staff")
        }
      )
    }
  }

  fun toggleStaffStatus(user: AdminUserEntity) {
    viewModelScope.launch {
      val currentUser = _currentAdminUser.value
      repository.toggleStaffStatus(user, currentUser?.name ?: "Owner")
      showMessage("Staff status updated.")
    }
  }

  // --- SECURITY ---
  fun changePassword(
    currentPass: String,
    newPass: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      val user = _currentAdminUser.value ?: run {
        onError("No logged in user")
        return@launch
      }
      val result = repository.changePassword(user.id, currentPass, newPass)
      result.fold(
        onSuccess = {
          showMessage("Password changed successfully.")
          onSuccess()
        },
        onFailure = {
          onError(it.localizedMessage ?: "Failed to change password")
        }
      )
    }
  }

  // --- CATEGORIES ---
  fun addCategory(category: CategoryEntity, onSuccess: () -> Unit) {
    viewModelScope.launch {
      repository.addCategory(category, _currentAdminUser.value?.name ?: "Admin")
      showMessage("Category ${category.name} added.")
      onSuccess()
    }
  }

  fun deleteCategory(category: CategoryEntity) {
    viewModelScope.launch {
      repository.deleteCategory(category, _currentAdminUser.value?.name ?: "Admin")
      showMessage("Category ${category.name} deleted.")
    }
  }
}
