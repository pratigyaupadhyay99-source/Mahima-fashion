package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ProductEntity::class,
    CategoryEntity::class,
    AdminUserEntity::class,
    CustomerEntity::class,
    SupplierEntity::class,
    OrderEnquiryEntity::class,
    InvoiceEntity::class,
    PaymentEntity::class,
    ExpenseEntity::class,
    InventoryLogEntity::class,
    AuditLogEntity::class,
    AppSettingEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class MahimaDatabase : RoomDatabase() {

  abstract fun fashionDao(): FashionDao

  companion object {
    @Volatile
    private var INSTANCE: MahimaDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): MahimaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MahimaDatabase::class.java,
          "mahima_fashion.db"
        )
          .addCallback(MahimaDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class MahimaDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.fashionDao())
          }
        }
      }

      private suspend fun populateInitialData(dao: FashionDao) {
        // Initial Categories
        val categories = listOf(
          CategoryEntity(name = "Abayas", description = "Luxury modest abayas and kaftans", iconName = "checkroom"),
          CategoryEntity(name = "Hijabs & Scarves", description = "Premium modal, chiffon & silk hijabs", iconName = "style"),
          CategoryEntity(name = "Unstitched Suits", description = "Pure fabrics with heavy dupattas", iconName = "inventory_2"),
          CategoryEntity(name = "Dress Materials", description = "Kashmiri, zari & chikankari materials", iconName = "texture"),
          CategoryEntity(name = "Modest Dresses", description = "Elegant flowy designer gowns", iconName = "loyalty"),
          CategoryEntity(name = "Dupattas", description = "Organza, silk & banarasi dupattas", iconName = "all_inclusive"),
          CategoryEntity(name = "Embroidered Luxury", description = "Heavy boutique party-wear collections", iconName = "stars"),
          CategoryEntity(name = "Printed Daily Wear", description = "Pure cambric and mulmul cotton suits", iconName = "brush"),
          CategoryEntity(name = "Festive & Party Wear", description = "Zari, thread & sequence collections", iconName = "celebration")
        )
        categories.forEach { dao.insertCategory(it) }

        // Initial Products (Dynamic wholesale modest fashion catalog)
        val sampleProducts = listOf(
          ProductEntity(
            name = "Royal Dubai Embroidered Abaya",
            productCode = "MF-ABY-101",
            category = "Abayas",
            collection = "Festive Luxury",
            thumbnail = "banner_fashion_hero",
            imagesJson = "banner_fashion_hero,ic_mahima_logo",
            description = "Crafted from premium imported Nida fabric with intricate hand-embroidered floral cuff and hem embellishments. Modest flare with matching Sheila hijab included.",
            fabric = "Premium Saudi Nida",
            color = "Imperial Emerald & Gold",
            design = "Hand Embroidered",
            size = "Free Size (Bust 44-50 inch, Length 56 inch)",
            purchasePrice = 1350.0,
            originalPrice = 2600.0,
            wholesalePrice = 1850.0,
            discountPercentage = 28.0,
            discountAmount = 750.0,
            finalPrice = 1850.0,
            gst = 5.0,
            stock = 45,
            minimumStock = 10,
            minimumOrderQuantity = 5,
            trending = true,
            featured = true,
            newArrival = true
          ),
          ProductEntity(
            name = "Kashmiri Aari Work Dress Material",
            productCode = "MF-DM-204",
            category = "Dress Materials",
            collection = "Artisan Heritage",
            thumbnail = "banner_fashion_hero",
            imagesJson = "banner_fashion_hero",
            description = "Three-piece unstitched dress material featuring delicate Kashmiri Aari thread work on neck and daman. Includes soft Pashmina blend bottom and embroidered dupatta.",
            fabric = "Pure Woolen Pashmina Silk",
            color = "Wine Burgundy & Antique Gold",
            design = "Kashmiri Aari Threadwork",
            size = "Top: 2.5m, Bottom: 2.5m, Dupatta: 2.25m",
            purchasePrice = 1050.0,
            originalPrice = 2100.0,
            wholesalePrice = 1450.0,
            discountPercentage = 30.0,
            discountAmount = 650.0,
            finalPrice = 1450.0,
            gst = 5.0,
            stock = 60,
            minimumStock = 15,
            minimumOrderQuantity = 10,
            trending = true,
            featured = false,
            newArrival = true
          ),
          ProductEntity(
            name = "Chanderi Silk Unstitched Salwar Suit",
            productCode = "MF-USS-305",
            category = "Unstitched Suits",
            collection = "Festive Elegance",
            thumbnail = "banner_fashion_hero",
            imagesJson = "banner_fashion_hero",
            description = "Rich Chanderi woven zari bootis with digital printed pure organza dupatta. Ideal for Eid, wedding guest wear, and boutique bridal collections.",
            fabric = "Pure Chanderi Silk",
            color = "Teal Blue & Champagne Gold",
            design = "Woven Zari Booti",
            size = "Top: 2.5m, Bottom: 2.5m, Dupatta: 2.4m",
            purchasePrice = 1200.0,
            originalPrice = 2400.0,
            wholesalePrice = 1650.0,
            discountPercentage = 31.0,
            discountAmount = 750.0,
            finalPrice = 1650.0,
            gst = 5.0,
            stock = 38,
            minimumStock = 8,
            minimumOrderQuantity = 8,
            trending = false,
            featured = true,
            newArrival = true
          ),
          ProductEntity(
            name = "Turkish Modal Silk Hijab Set (Bundle of 6)",
            productCode = "MF-HJB-401",
            category = "Hijabs & Scarves",
            collection = "Daily Premium",
            thumbnail = "ic_mahima_logo",
            imagesJson = "ic_mahima_logo",
            description = "Ultra-breathable Turkish modal blend hijabs with non-slip texture, featherlight drape, and luxury metallic hem finish. Assorted wholesale color pack.",
            fabric = "Turkish Modal & Lyocell Blend",
            color = "Assorted Pastel & Earthy Tones",
            design = "Solid Shimmer Hem",
            size = "185 cm x 75 cm",
            purchasePrice = 650.0,
            originalPrice = 1500.0,
            wholesalePrice = 950.0,
            discountPercentage = 36.0,
            discountAmount = 550.0,
            finalPrice = 950.0,
            gst = 5.0,
            stock = 85,
            minimumStock = 20,
            minimumOrderQuantity = 12,
            trending = true,
            featured = true,
            newArrival = false
          ),
          ProductEntity(
            name = "Lucknowi Chikankari Georgette Suit Set",
            productCode = "MF-LKN-812",
            category = "Embroidered Luxury",
            collection = "Boutique Signature",
            thumbnail = "banner_fashion_hero",
            imagesJson = "banner_fashion_hero",
            description = "Intricate handmade Bakhiya and Phanda Chikankari needlework with subtle tone-on-tone sequence embellishments. Premium dyeable viscose georgette fabric.",
            fabric = "Pure Viscose Georgette",
            color = "Powder Peach & Silver",
            design = "Handcrafted Chikankari",
            size = "Unstitched 3-Piece Set",
            purchasePrice = 1450.0,
            originalPrice = 2900.0,
            wholesalePrice = 1950.0,
            discountPercentage = 32.0,
            discountAmount = 950.0,
            finalPrice = 1950.0,
            gst = 5.0,
            stock = 25,
            minimumStock = 5,
            minimumOrderQuantity = 6,
            trending = true,
            featured = true,
            newArrival = true
          ),
          ProductEntity(
            name = "Festive Velvet Modest Kaftan Gown",
            productCode = "MF-KFT-607",
            category = "Modest Dresses",
            collection = "Winter Royal",
            thumbnail = "banner_fashion_hero",
            imagesJson = "banner_fashion_hero",
            description = "Micro-velvet 9000 fabric with rich antique dori and zardozi neck motif. Inner drawstring for flattering yet modest silhouette adjustment.",
            fabric = "Micro Velvet 9000",
            color = "Royal Navy Blue",
            design = "Zardozi & Dori Embellished",
            size = "Free Size Flowy Cut",
            purchasePrice = 1600.0,
            originalPrice = 3200.0,
            wholesalePrice = 2200.0,
            discountPercentage = 31.0,
            discountAmount = 1000.0,
            finalPrice = 2200.0,
            gst = 5.0,
            stock = 18,
            minimumStock = 5,
            minimumOrderQuantity = 6,
            trending = false,
            featured = true,
            newArrival = false
          )
        )
        sampleProducts.forEach { dao.insertProduct(it) }

        // Initial Customers (Boutiques & Wholesalers)
        val initialCustomers = listOf(
          CustomerEntity(
            name = "Ayesha Khan",
            businessName = "Noor Modest Boutique",
            mobile = "+91 98234 11223",
            address = "Linking Road, Bandra West, Mumbai, MH",
            gstNumber = "27AABCN1234D1Z2",
            totalPurchases = 48500.0,
            amountPaid = 40000.0,
            outstandingAmount = 8500.0
          ),
          CustomerEntity(
            name = "Mohammed Tariq",
            businessName = "Al-Barakah Cloth Stores",
            mobile = "+91 97123 44556",
            address = "Commercial Street, Bangalore, KA",
            gstNumber = "29ABCDE5678F1Z5",
            totalPurchases = 76000.0,
            amountPaid = 76000.0,
            outstandingAmount = 0.0
          )
        )
        initialCustomers.forEach { dao.insertCustomer(it) }

        // Initial Suppliers
        val initialSuppliers = listOf(
          SupplierEntity(
            name = "Surat Nida Mills Ltd.",
            company = "Surat Nida & Abaya Fabrics",
            mobile = "+91 98980 12345",
            address = "Ring Road Textile Market, Surat, GJ",
            gstNumber = "24AABCS9988E1Z1",
            productsSupplied = "Imported Nida, Saudi Crepe",
            amountPayable = 25000.0
          ),
          SupplierEntity(
            name = "Kashmir Weaves Corporation",
            company = "Heritage Kashmiri Textiles",
            mobile = "+91 99060 67890",
            address = "Lal Chowk, Srinagar, JK",
            gstNumber = "01AAACK4433B1Z9",
            productsSupplied = "Aari Suits, Pashmina Fabrics",
            amountPayable = 18000.0
          )
        )
        initialSuppliers.forEach { dao.insertSupplier(it) }

        // Initial App Settings
        val settings = listOf(
          AppSettingEntity("business_name", "MAHIMA FASHION"),
          AppSettingEntity("business_phone", "+91 9327607195"),
          AppSettingEntity("whatsapp_number", "919327607195"),
          AppSettingEntity("business_email", "wholesale@mahimafashion.com"),
          AppSettingEntity("business_address", "2070, New Pashupati Textile Market, Opp. Shyam Market, Moti Begam Wadi, Ring Road, Surat, Gujarat"),
          AppSettingEntity("business_gstin", "24AAACM1234F1Z8"),
          AppSettingEntity("currency_symbol", "₹"),
          AppSettingEntity("invoice_prefix", "MF-INV-")
        )
        settings.forEach { dao.setSetting(it) }

        // Initial Audit Log
        dao.insertAuditLog(
          AuditLogEntity(
            userName = "System",
            userRole = "SYSTEM",
            action = "DATABASE_INITIALIZED",
            details = "Mahima Fashion wholesale database initialized with modest fashion collections.",
            timestamp = System.currentTimeMillis()
          )
        )
      }
    }
  }
}
