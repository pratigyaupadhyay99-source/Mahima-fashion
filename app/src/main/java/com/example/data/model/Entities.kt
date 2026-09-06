package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val productCode: String,
  val category: String,
  val collection: String = "All",
  val thumbnail: String, // Resource name or URI
  val imagesJson: String = "", // Comma-separated or JSON list of image names
  val description: String,
  val fabric: String,
  val color: String,
  val design: String = "Embroidered",
  val size: String = "Free Size / Unstitched",
  val purchasePrice: Double,
  val originalPrice: Double,
  val wholesalePrice: Double,
  val discountPercentage: Double,
  val discountAmount: Double,
  val finalPrice: Double,
  val gst: Double = 5.0,
  val stock: Int,
  val minimumStock: Int = 10,
  val minimumOrderQuantity: Int = 10,
  val trending: Boolean = false,
  val featured: Boolean = false,
  val newArrival: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val description: String = "",
  val iconName: String = "category",
  val imageUrl: String = ""
)

@Entity(tableName = "admin_users")
data class AdminUserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val emailOrMobile: String,
  val passwordHash: String,
  val salt: String,
  val role: String, // "OWNER", "MANAGER", "SALES", "INVENTORY", "ACCOUNTANT", "CUSTOM"
  val customPermissions: String = "", // JSON or comma-separated permissions
  val isActive: Boolean = true,
  val lastLoginAt: Long = 0L,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val businessName: String,
  val mobile: String,
  val address: String,
  val gstNumber: String = "",
  val totalPurchases: Double = 0.0,
  val amountPaid: Double = 0.0,
  val outstandingAmount: Double = 0.0,
  val lastOrderAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val company: String,
  val mobile: String,
  val address: String,
  val gstNumber: String = "",
  val productsSupplied: String = "",
  val amountPayable: Double = 0.0
)

@Entity(tableName = "order_enquiries")
data class OrderEnquiryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val customerName: String,
  val businessName: String,
  val mobile: String,
  val address: String = "",
  val productId: Long,
  val productName: String,
  val productCode: String,
  val quantity: Int,
  val historicalPrice: Double,
  val totalAmount: Double,
  val message: String = "",
  val status: String = "New", // "New", "Contacted", "Confirmed", "Completed", "Cancelled"
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val invoiceNumber: String,
  val customerName: String,
  val businessName: String,
  val customerMobile: String,
  val itemsJson: String, // Storing serialized items with exact historical price!
  val subtotal: Double,
  val discount: Double,
  val gstAmount: Double,
  val totalAmount: Double,
  val amountPaid: Double,
  val balanceDue: Double,
  val paymentMethod: String = "UPI", // Cash, UPI, Bank Transfer, Card, Credit
  val paymentStatus: String = "Paid", // Paid, Partially Paid, Pending
  val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val invoiceNumber: String,
  val customerName: String,
  val amount: Double,
  val paymentMethod: String,
  val date: Long = System.currentTimeMillis(),
  val notes: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val category: String, // Rent, Transport, Packaging, Electricity, Salary, Marketing, Other
  val amount: Double,
  val description: String,
  val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_logs")
data class InventoryLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productName: String,
  val sku: String,
  val changeAmount: Int,
  val previousStock: Int,
  val newStock: Int,
  val reason: String,
  val performedBy: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userName: String,
  val userRole: String,
  val action: String,
  val details: String,
  val previousValue: String = "",
  val newValue: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
  @PrimaryKey val key: String,
  val value: String
)
