package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface FashionDao {

  // --- PRODUCTS ---
  @Query("SELECT * FROM products ORDER BY updatedAt DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  fun getProductByIdFlow(id: Long): Flow<ProductEntity?>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: Long): ProductEntity?

  @Query("SELECT * FROM products WHERE trending = 1 ORDER BY updatedAt DESC")
  fun getTrendingProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE newArrival = 1 ORDER BY updatedAt DESC")
  fun getNewArrivals(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE featured = 1 ORDER BY updatedAt DESC")
  fun getFeaturedProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE discountPercentage > 0 ORDER BY discountPercentage DESC")
  fun getDiscountedProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE category = :category ORDER BY updatedAt DESC")
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity): Long

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Delete
  suspend fun deleteProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteProductById(id: Long)

  @Query("UPDATE products SET stock = :newStock, updatedAt = :time WHERE id = :id")
  suspend fun updateProductStock(id: Long, newStock: Int, time: Long = System.currentTimeMillis())

  // --- CATEGORIES ---
  @Query("SELECT * FROM categories ORDER BY name ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

  @Delete
  suspend fun deleteCategory(category: CategoryEntity)

  // --- ADMIN & STAFF ---
  @Query("SELECT * FROM admin_users WHERE role = 'OWNER' LIMIT 1")
  suspend fun getOwner(): AdminUserEntity?

  @Query("SELECT COUNT(*) FROM admin_users")
  suspend fun getAdminCount(): Int

  @Query("SELECT * FROM admin_users WHERE emailOrMobile = :identity LIMIT 1")
  suspend fun getUserByEmailOrMobile(identity: String): AdminUserEntity?

  @Query("SELECT * FROM admin_users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<AdminUserEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: AdminUserEntity): Long

  @Update
  suspend fun updateUser(user: AdminUserEntity)

  // --- ORDERS / ENQUIRIES ---
  @Query("SELECT * FROM order_enquiries ORDER BY createdAt DESC")
  fun getAllEnquiries(): Flow<List<OrderEnquiryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEnquiry(enquiry: OrderEnquiryEntity): Long

  @Query("UPDATE order_enquiries SET status = :status WHERE id = :id")
  suspend fun updateEnquiryStatus(id: Long, status: String)

  // --- CUSTOMERS ---
  @Query("SELECT * FROM customers ORDER BY lastOrderAt DESC")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity): Long

  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  // --- SUPPLIERS ---
  @Query("SELECT * FROM suppliers ORDER BY name ASC")
  fun getAllSuppliers(): Flow<List<SupplierEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSupplier(supplier: SupplierEntity): Long

  @Delete
  suspend fun deleteSupplier(supplier: SupplierEntity)

  // --- INVOICES / BILLING ---
  @Query("SELECT * FROM invoices ORDER BY date DESC")
  fun getAllInvoices(): Flow<List<InvoiceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInvoice(invoice: InvoiceEntity): Long

  // --- PAYMENTS ---
  @Query("SELECT * FROM payments ORDER BY date DESC")
  fun getAllPayments(): Flow<List<PaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPayment(payment: PaymentEntity): Long

  // --- EXPENSES ---
  @Query("SELECT * FROM expenses ORDER BY date DESC")
  fun getAllExpenses(): Flow<List<ExpenseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity): Long

  // --- INVENTORY LOGS ---
  @Query("SELECT * FROM inventory_logs ORDER BY timestamp DESC")
  fun getAllInventoryLogs(): Flow<List<InventoryLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInventoryLog(log: InventoryLogEntity): Long

  // --- AUDIT LOGS ---
  @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
  fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAuditLog(log: AuditLogEntity): Long

  // --- SETTINGS ---
  @Query("SELECT value FROM app_settings WHERE key = :key LIMIT 1")
  suspend fun getSetting(key: String): String?

  @Query("SELECT * FROM app_settings")
  fun getAllSettings(): Flow<List<AppSettingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setSetting(setting: AppSettingEntity)
}
