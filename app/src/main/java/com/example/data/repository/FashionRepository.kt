package com.example.data.repository

import com.example.data.dao.FashionDao
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
import com.example.data.security.PasswordSecurity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FashionRepository(private val dao: FashionDao) {

  // Products
  val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
  val trendingProducts: Flow<List<ProductEntity>> = dao.getTrendingProducts()
  val newArrivals: Flow<List<ProductEntity>> = dao.getNewArrivals()
  val featuredProducts: Flow<List<ProductEntity>> = dao.getFeaturedProducts()
  val discountedProducts: Flow<List<ProductEntity>> = dao.getDiscountedProducts()
  val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()

  // Business & Admin Flows
  val allEnquiries: Flow<List<OrderEnquiryEntity>> = dao.getAllEnquiries()
  val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
  val allSuppliers: Flow<List<SupplierEntity>> = dao.getAllSuppliers()
  val allInvoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()
  val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
  val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
  val allInventoryLogs: Flow<List<InventoryLogEntity>> = dao.getAllInventoryLogs()
  val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
  val allStaffUsers: Flow<List<AdminUserEntity>> = dao.getAllUsers()
  val allSettings: Flow<List<AppSettingEntity>> = dao.getAllSettings()

  fun getProductByIdFlow(id: Long): Flow<ProductEntity?> = dao.getProductByIdFlow(id)

  suspend fun getProductById(id: Long): ProductEntity? = withContext(Dispatchers.IO) {
    dao.getProductById(id)
  }

  // --- AUTHENTICATION & SETUP ---
  suspend fun hasOwnerAccount(): Boolean = withContext(Dispatchers.IO) {
    dao.getAdminCount() > 0
  }

  suspend fun registerOwner(
    name: String,
    emailOrMobile: String,
    password: String
  ): Result<AdminUserEntity> = withContext(Dispatchers.IO) {
    val validation = PasswordSecurity.validatePassword(password)
    if (!validation.isValid) {
      return@withContext Result.failure(
        IllegalArgumentException("Password must be at least 8 chars and include uppercase, lowercase, number, and special character.")
      )
    }

    val existingUser = dao.getUserByEmailOrMobile(emailOrMobile.trim())
    if (existingUser != null) {
      return@withContext Result.failure(IllegalArgumentException("An account with this email/mobile already exists."))
    }

    val salt = PasswordSecurity.generateSalt()
    val hash = PasswordSecurity.hashPassword(password, salt)

    val owner = AdminUserEntity(
      name = name.trim(),
      emailOrMobile = emailOrMobile.trim(),
      passwordHash = hash,
      salt = salt,
      role = "OWNER",
      customPermissions = "ALL",
      isActive = true,
      lastLoginAt = System.currentTimeMillis()
    )

    val id = dao.insertUser(owner)
    val createdOwner = owner.copy(id = id)

    dao.setSetting(AppSettingEntity("setupCompleted", "true"))
    dao.insertAuditLog(
      AuditLogEntity(
        userName = createdOwner.name,
        userRole = "OWNER",
        action = "ADMIN_SETUP_COMPLETED",
        details = "Owner/Super Admin account created successfully for ${createdOwner.emailOrMobile}."
      )
    )

    Result.success(createdOwner)
  }

  suspend fun loginAdmin(
    emailOrMobile: String,
    password: String
  ): Result<AdminUserEntity> = withContext(Dispatchers.IO) {
    val user = dao.getUserByEmailOrMobile(emailOrMobile.trim())
      ?: return@withContext Result.failure(IllegalArgumentException("Invalid email/mobile or password."))

    if (!user.isActive) {
      return@withContext Result.failure(IllegalStateException("This account is currently disabled. Please contact the Owner."))
    }

    val isMatch = PasswordSecurity.verifyPassword(password, user.salt, user.passwordHash)
    if (!isMatch) {
      return@withContext Result.failure(IllegalArgumentException("Invalid email/mobile or password."))
    }

    val updatedUser = user.copy(lastLoginAt = System.currentTimeMillis())
    dao.updateUser(updatedUser)

    dao.insertAuditLog(
      AuditLogEntity(
        userName = user.name,
        userRole = user.role,
        action = "LOGIN_SUCCESS",
        details = "Admin user logged in successfully."
      )
    )

    Result.success(updatedUser)
  }

  suspend fun changePassword(
    userId: Long,
    currentPass: String,
    newPass: String
  ): Result<Unit> = withContext(Dispatchers.IO) {
    val users = dao.getAllUsers()
    // Find user
    val targetUser = dao.getUserByEmailOrMobile("") // helper or search
    // Direct search by query in DB:
    val adminList = mutableListOf<AdminUserEntity>()
    // Let's get user directly
    val user = dao.getOwner()?.takeIf { it.id == userId } ?: dao.getUserByEmailOrMobile(dao.getSetting("last_login_user") ?: "")
    val activeUser = user ?: return@withContext Result.failure(IllegalArgumentException("User not found."))

    if (!PasswordSecurity.verifyPassword(currentPass, activeUser.salt, activeUser.passwordHash)) {
      return@withContext Result.failure(IllegalArgumentException("Current password is incorrect."))
    }

    val validation = PasswordSecurity.validatePassword(newPass)
    if (!validation.isValid) {
      return@withContext Result.failure(IllegalArgumentException("New password does not meet security requirements."))
    }

    val newSalt = PasswordSecurity.generateSalt()
    val newHash = PasswordSecurity.hashPassword(newPass, newSalt)
    val updated = activeUser.copy(passwordHash = newHash, salt = newSalt)
    dao.updateUser(updated)

    dao.insertAuditLog(
      AuditLogEntity(
        userName = updated.name,
        userRole = updated.role,
        action = "PASSWORD_CHANGED",
        details = "Password updated securely."
      )
    )

    Result.success(Unit)
  }

  // --- PRODUCT MANAGEMENT ---
  suspend fun saveProduct(
    product: ProductEntity,
    operatorName: String,
    operatorRole: String
  ): Long = withContext(Dispatchers.IO) {
    val isEdit = product.id > 0
    var previousProduct: ProductEntity? = null
    if (isEdit) {
      previousProduct = dao.getProductById(product.id)
    }

    val id = dao.insertProduct(product)

    if (isEdit && previousProduct != null) {
      // Check price change
      if (previousProduct.wholesalePrice != product.wholesalePrice || previousProduct.finalPrice != product.finalPrice) {
        dao.insertAuditLog(
          AuditLogEntity(
            userName = operatorName,
            userRole = operatorRole,
            action = "PRICE_CHANGED",
            details = "Product ${product.name} (${product.productCode}) price updated.",
            previousValue = "Wholesale: ₹${previousProduct.wholesalePrice}, Final: ₹${previousProduct.finalPrice}",
            newValue = "Wholesale: ₹${product.wholesalePrice}, Final: ₹${product.finalPrice}"
          )
        )
      }
      // Check stock change
      if (previousProduct.stock != product.stock) {
        val diff = product.stock - previousProduct.stock
        dao.insertInventoryLog(
          InventoryLogEntity(
            productId = product.id,
            productName = product.name,
            sku = product.productCode,
            changeAmount = diff,
            previousStock = previousProduct.stock,
            newStock = product.stock,
            reason = "Manual Edit by $operatorName",
            performedBy = operatorName
          )
        )
      }
    } else {
      dao.insertInventoryLog(
        InventoryLogEntity(
          productId = id,
          productName = product.name,
          sku = product.productCode,
          changeAmount = product.stock,
          previousStock = 0,
          newStock = product.stock,
          reason = "Initial Stock Added",
          performedBy = operatorName
        )
      )
      dao.insertAuditLog(
        AuditLogEntity(
          userName = operatorName,
          userRole = operatorRole,
          action = "PRODUCT_CREATED",
          details = "Created product ${product.name} (SKU: ${product.productCode})."
        )
      )
    }
    id
  }

  suspend fun deleteProduct(
    product: ProductEntity,
    operatorName: String,
    operatorRole: String
  ) = withContext(Dispatchers.IO) {
    dao.deleteProduct(product)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = operatorRole,
        action = "PRODUCT_DELETED",
        details = "Deleted product ${product.name} (SKU: ${product.productCode}).",
        previousValue = "Stock: ${product.stock}, Price: ₹${product.finalPrice}"
      )
    )
  }

  suspend fun adjustStock(
    productId: Long,
    amountDelta: Int,
    reason: String,
    operatorName: String,
    operatorRole: String
  ) = withContext(Dispatchers.IO) {
    val product = dao.getProductById(productId) ?: return@withContext
    val newStock = (product.stock + amountDelta).coerceAtLeast(0)
    dao.updateProductStock(productId, newStock)

    dao.insertInventoryLog(
      InventoryLogEntity(
        productId = productId,
        productName = product.name,
        sku = product.productCode,
        changeAmount = amountDelta,
        previousStock = product.stock,
        newStock = newStock,
        reason = reason,
        performedBy = operatorName
      )
    )

    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = operatorRole,
        action = "STOCK_ADJUSTED",
        details = "Product ${product.name} stock changed by $amountDelta ($reason).",
        previousValue = "Stock: ${product.stock}",
        newValue = "Stock: $newStock"
      )
    )
  }

  // --- CATEGORIES ---
  suspend fun addCategory(category: CategoryEntity, operator: String) = withContext(Dispatchers.IO) {
    dao.insertCategory(category)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operator,
        userRole = "ADMIN",
        action = "CATEGORY_ADDED",
        details = "Added category: ${category.name}"
      )
    )
  }

  suspend fun deleteCategory(category: CategoryEntity, operator: String) = withContext(Dispatchers.IO) {
    dao.deleteCategory(category)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operator,
        userRole = "ADMIN",
        action = "CATEGORY_DELETED",
        details = "Deleted category: ${category.name}"
      )
    )
  }

  // --- ORDERS & ENQUIRIES ---
  suspend fun submitCustomerEnquiry(enquiry: OrderEnquiryEntity): Long = withContext(Dispatchers.IO) {
    val id = dao.insertEnquiry(enquiry)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = enquiry.customerName,
        userRole = "CUSTOMER",
        action = "WHOLESALE_ENQUIRY_SUBMITTED",
        details = "Wholesale enquiry for ${enquiry.quantity}x ${enquiry.productName} (₹${enquiry.totalAmount})."
      )
    )
    id
  }

  suspend fun updateEnquiryStatus(
    enquiryId: Long,
    newStatus: String,
    operatorName: String,
    operatorRole: String
  ) = withContext(Dispatchers.IO) {
    dao.updateEnquiryStatus(enquiryId, newStatus)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = operatorRole,
        action = "ENQUIRY_STATUS_UPDATED",
        details = "Enquiry #$enquiryId updated to $newStatus."
      )
    )
  }

  // --- BILLING / INVOICING ---
  suspend fun createInvoice(
    invoice: InvoiceEntity,
    operatorName: String,
    operatorRole: String
  ): Long = withContext(Dispatchers.IO) {
    val id = dao.insertInvoice(invoice)

    if (invoice.amountPaid > 0) {
      dao.insertPayment(
        PaymentEntity(
          invoiceNumber = invoice.invoiceNumber,
          customerName = invoice.customerName,
          amount = invoice.amountPaid,
          paymentMethod = invoice.paymentMethod,
          notes = "Payment received upon invoice generation."
        )
      )
    }

    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = operatorRole,
        action = "INVOICE_GENERATED",
        details = "Generated Invoice #${invoice.invoiceNumber} for ${invoice.customerName} total ₹${invoice.totalAmount}."
      )
    )
    id
  }

  // --- PAYMENTS & EXPENSES ---
  suspend fun recordPayment(payment: PaymentEntity, operatorName: String) = withContext(Dispatchers.IO) {
    dao.insertPayment(payment)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = "FINANCE",
        action = "PAYMENT_RECORDED",
        details = "Recorded payment of ₹${payment.amount} for ${payment.customerName} (${payment.paymentMethod})."
      )
    )
  }

  suspend fun recordExpense(expense: ExpenseEntity, operatorName: String) = withContext(Dispatchers.IO) {
    dao.insertExpense(expense)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = "FINANCE",
        action = "EXPENSE_RECORDED",
        details = "Recorded expense of ₹${expense.amount} under ${expense.category}: ${expense.description}."
      )
    )
  }

  // --- CUSTOMERS & SUPPLIERS ---
  suspend fun saveCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) {
    dao.insertCustomer(customer)
  }

  suspend fun saveSupplier(supplier: SupplierEntity) = withContext(Dispatchers.IO) {
    dao.insertSupplier(supplier)
  }

  // --- STAFF & RBAC ---
  suspend fun addStaff(
    name: String,
    emailOrMobile: String,
    password: String,
    role: String,
    customPermissions: String,
    operatorName: String
  ): Result<AdminUserEntity> = withContext(Dispatchers.IO) {
    val existing = dao.getUserByEmailOrMobile(emailOrMobile.trim())
    if (existing != null) {
      return@withContext Result.failure(IllegalArgumentException("Staff member with this mobile/email already exists."))
    }
    val salt = PasswordSecurity.generateSalt()
    val hash = PasswordSecurity.hashPassword(password, salt)
    val user = AdminUserEntity(
      name = name.trim(),
      emailOrMobile = emailOrMobile.trim(),
      passwordHash = hash,
      salt = salt,
      role = role,
      customPermissions = customPermissions,
      isActive = true
    )
    val id = dao.insertUser(user)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = "OWNER",
        action = "STAFF_ADDED",
        details = "Added staff $name with role $role."
      )
    )
    Result.success(user.copy(id = id))
  }

  suspend fun toggleStaffStatus(user: AdminUserEntity, operatorName: String) = withContext(Dispatchers.IO) {
    val updated = user.copy(isActive = !user.isActive)
    dao.updateUser(updated)
    dao.insertAuditLog(
      AuditLogEntity(
        userName = operatorName,
        userRole = "OWNER",
        action = if (updated.isActive) "STAFF_ACTIVATED" else "STAFF_DISABLED",
        details = "${user.name}'s account status set to active=${updated.isActive}."
      )
    )
  }

  suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
    dao.setSetting(AppSettingEntity(key, value))
  }
}
