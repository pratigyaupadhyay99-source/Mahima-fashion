package com.example.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.FashionViewModel
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
fun AdminProductsScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val products by viewModel.products.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()

  var searchQuery by remember { mutableStateOf("") }
  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
  var isAddingProduct by remember { mutableStateOf(false) }
  var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

  val filtered = products.filter {
    searchQuery.isBlank() ||
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.productCode.contains(searchQuery, ignoreCase = true) ||
      it.category.contains(searchQuery, ignoreCase = true)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Header
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
            text = "Product Catalog Management",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Search & Summary
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, SKU or category...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Total: ${products.size} products | ${products.sumOf { it.stock }} pcs",
            fontSize = 11.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Showing: ${filtered.size}",
            fontSize = 11.sp,
            color = Emerald700,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Products List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filtered, key = { it.id }) { prod ->
          AdminProductRow(
            product = prod,
            onEdit = { editingProduct = prod },
            onDelete = { productToDelete = prod }
          )
        }
      }
    }

    // Floating Add Button
    FloatingActionButton(
      onClick = { isAddingProduct = true },
      containerColor = GoldMetallic,
      contentColor = Emerald900,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
    }

    // Add / Edit Product Dialog
    if (isAddingProduct || editingProduct != null) {
      ProductFormDialog(
        product = editingProduct,
        categories = categories,
        onDismiss = {
          isAddingProduct = false
          editingProduct = null
        },
        onSave = { updated ->
          viewModel.saveProduct(updated) {
            isAddingProduct = false
            editingProduct = null
          }
        }
      )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
      AlertDialog(
        onDismissRequest = { productToDelete = null },
        title = { Text("Delete Product?") },
        text = {
          Text("Are you sure you want to delete '${productToDelete?.name}' (${productToDelete?.productCode})? This action cannot be undone.")
        },
        confirmButton = {
          Button(
            onClick = {
              productToDelete?.let { viewModel.deleteProduct(it) }
              productToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = StockRed)
          ) {
            Text("Delete", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { productToDelete = null }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}

@Composable
fun AdminProductRow(
  product: ProductEntity,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail
      Image(
        painter = painterResource(
          if (product.thumbnail == "ic_mahima_logo") R.drawable.ic_mahima_logo else R.drawable.banner_fashion_hero
        ),
        contentDescription = product.name,
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(6.dp)),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = product.productCode,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GoldDark
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = product.category,
            fontSize = 10.sp,
            color = Color.Gray
          )
        }

        Text(
          text = product.name,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )

        Text(
          text = "Wholesale: ₹${product.wholesalePrice.toInt()} | MRP: ₹${product.originalPrice.toInt()} | Purchase: ₹${product.purchasePrice.toInt()}",
          fontSize = 11.sp,
          color = Emerald700,
          fontWeight = FontWeight.SemiBold
        )

        Row(
          modifier = Modifier.padding(top = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          val (stockLabel, stockColor) = when {
            product.stock <= 0 -> "Out of Stock" to StockRed
            product.stock <= product.minimumStock -> "Low: ${product.stock} pcs" to StockOrange
            else -> "Stock: ${product.stock} pcs" to StockGreen
          }
          Text(text = stockLabel, fontSize = 10.sp, color = stockColor, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "MOQ: ${product.minimumOrderQuantity}", fontSize = 10.sp, color = Color.Gray)
        }
      }

      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Emerald700, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StockRed, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
  product: ProductEntity?,
  categories: List<CategoryEntity>,
  onDismiss: () -> Unit,
  onSave: (ProductEntity) -> Unit
) {
  val isEdit = product != null

  var name by remember { mutableStateOf(product?.name ?: "") }
  var productCode by remember { mutableStateOf(product?.productCode ?: "MF-") }
  var category by remember { mutableStateOf(product?.category ?: categories.firstOrNull()?.name ?: "Abayas") }
  var collection by remember { mutableStateOf(product?.collection ?: "Daily Modest") }
  var fabric by remember { mutableStateOf(product?.fabric ?: "Saudi Nida") }
  var color by remember { mutableStateOf(product?.color ?: "Black") }
  var design by remember { mutableStateOf(product?.design ?: "Embroidered") }
  var size by remember { mutableStateOf(product?.size ?: "Free Size") }
  var description by remember { mutableStateOf(product?.description ?: "") }

  var purchasePriceText by remember { mutableStateOf(product?.purchasePrice?.toString() ?: "1000") }
  var originalPriceText by remember { mutableStateOf(product?.originalPrice?.toString() ?: "2000") }
  var wholesalePriceText by remember { mutableStateOf(product?.wholesalePrice?.toString() ?: "1500") }
  var discountPercentText by remember { mutableStateOf(product?.discountPercentage?.toString() ?: "25") }

  var stockText by remember { mutableStateOf(product?.stock?.toString() ?: "20") }
  var minStockText by remember { mutableStateOf(product?.minimumStock?.toString() ?: "5") }
  var moqText by remember { mutableStateOf(product?.minimumOrderQuantity?.toString() ?: "5") }

  var trending by remember { mutableStateOf(product?.trending ?: false) }
  var featured by remember { mutableStateOf(product?.featured ?: false) }
  var newArrival by remember { mutableStateOf(product?.newArrival ?: true) }
  var thumbnail by remember { mutableStateOf(product?.thumbnail ?: "banner_fashion_hero") }

  var categoryExpanded by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        Text(
          text = if (isEdit) "Edit Product Details" else "Add New Wholesale Product",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Emerald900
        )
        Text(
          text = "All changes will be permanently stored and logged in the audit trail.",
          fontSize = 10.sp,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Product Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = productCode,
            onValueChange = { productCode = it },
            label = { Text("Product SKU Code *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )

          // Category Selector
          ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded },
            modifier = Modifier.weight(1f)
          ) {
            OutlinedTextField(
              value = category,
              onValueChange = {},
              readOnly = true,
              label = { Text("Category") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
              modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = categoryExpanded,
              onDismissRequest = { categoryExpanded = false }
            ) {
              categories.forEach { cat ->
                DropdownMenuItem(
                  text = { Text(cat.name) },
                  onClick = {
                    category = cat.name
                    categoryExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = fabric,
            onValueChange = { fabric = it },
            label = { Text("Fabric Material") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color / Shade") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = design,
            onValueChange = { design = it },
            label = { Text("Design / Work") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = size,
            onValueChange = { size = it },
            label = { Text("Cut / Size") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Pricing & Wholesale Rates (₹)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = wholesalePriceText,
            onValueChange = { wholesalePriceText = it },
            label = { Text("Wholesale (₹) *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = originalPriceText,
            onValueChange = { originalPriceText = it },
            label = { Text("MRP / Retail (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = purchasePriceText,
            onValueChange = { purchasePriceText = it },
            label = { Text("Cost Price (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = discountPercentText,
            onValueChange = { discountPercentText = it },
            label = { Text("Discount %") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Stock & Order Limits", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = stockText,
            onValueChange = { stockText = it },
            label = { Text("Current Stock *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = minStockText,
            onValueChange = { minStockText = it },
            label = { Text("Low Alert") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = moqText,
            onValueChange = { moqText = it },
            label = { Text("MOQ *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Badges selection
        Text("Catalog Badges", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Emerald900)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = newArrival, onCheckedChange = { newArrival = it })
          Text("New Arrival", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Checkbox(checked = trending, onCheckedChange = { trending = it })
          Text("Trending", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Checkbox(checked = featured, onCheckedChange = { featured = it })
          Text("Featured", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) { Text("Cancel") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val wPrice = wholesalePriceText.toDoubleOrNull() ?: 1000.0
              val mPrice = originalPriceText.toDoubleOrNull() ?: (wPrice * 1.3)
              val pPrice = purchasePriceText.toDoubleOrNull() ?: (wPrice * 0.7)
              val dPercent = discountPercentText.toDoubleOrNull() ?: 0.0
              val stock = stockText.toIntOrNull() ?: 10
              val minStock = minStockText.toIntOrNull() ?: 5
              val moq = moqText.toIntOrNull() ?: 5

              val updated = ProductEntity(
                id = product?.id ?: 0,
                name = name.ifBlank { "Modest Dress" },
                productCode = productCode.ifBlank { "MF-GEN-01" },
                category = category,
                collection = collection,
                thumbnail = thumbnail,
                imagesJson = thumbnail,
                description = description,
                fabric = fabric,
                color = color,
                design = design,
                size = size,
                purchasePrice = pPrice,
                originalPrice = mPrice,
                wholesalePrice = wPrice,
                discountPercentage = dPercent,
                discountAmount = (mPrice * (dPercent / 100.0)),
                finalPrice = wPrice,
                gst = 5.0,
                stock = stock,
                minimumStock = minStock,
                minimumOrderQuantity = moq,
                trending = trending,
                featured = featured,
                newArrival = newArrival
              )
              onSave(updated)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
          ) {
            Text("Save Product", color = GoldShimmer, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
