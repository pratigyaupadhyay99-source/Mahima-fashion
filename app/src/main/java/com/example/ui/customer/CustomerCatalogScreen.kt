package com.example.ui.customer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FashionViewModel
import com.example.ui.components.ProductCard
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer

@Composable
fun CustomerCatalogScreen(
  viewModel: FashionViewModel,
  onOpenProduct: (Long) -> Unit
) {
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
  val selectedBadge by viewModel.selectedBadgeFilter.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Search Bar
    Surface(
      color = Emerald900,
      shadowElevation = 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.searchQuery.value = it },
          placeholder = { Text("Search by name, SKU, fabric, color...", fontSize = 12.sp, color = Color.Gray) },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Emerald700)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
              }
            }
          },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = GoldMetallic,
            unfocusedBorderColor = Color.Transparent
          )
        )
      }
    }

    // 2. Horizontal Category Filter Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        FilterChipTab(
          label = "All Categories",
          isSelected = selectedCategory == "All",
          onClick = { viewModel.selectedCategoryFilter.value = "All" }
        )
      }
      items(categories) { cat ->
        FilterChipTab(
          label = cat.name,
          isSelected = selectedCategory.equals(cat.name, ignoreCase = true),
          onClick = { viewModel.selectedCategoryFilter.value = cat.name }
        )
      }
    }

    // 3. Collection / Badge Sub-filters
    val badges = listOf("All", "Trending", "New Arrival", "Featured", "Discount")
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(badges) { badge ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (selectedBadge == badge) Emerald700 else MaterialTheme.colorScheme.surface,
          shadowElevation = 1.dp,
          modifier = Modifier.clickable { viewModel.selectedBadgeFilter.value = badge }
        ) {
          Text(
            text = badge,
            fontSize = 11.sp,
            fontWeight = if (selectedBadge == badge) FontWeight.Bold else FontWeight.Medium,
            color = if (selectedBadge == badge) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    // 4. Products Count & Summary
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${filteredProducts.size} Wholesale Products Available",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 5. 2-Column Fashion Grid
    if (filteredProducts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "No products found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald900
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Try adjusting your search or category filter.",
            fontSize = 12.sp,
            color = Color.Gray
          )
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredProducts, key = { it.id }) { product ->
          ProductCard(
            product = product,
            onClick = { onOpenProduct(product.id) },
            onAddToCart = { viewModel.addToEnquiryCart(product) }
          )
        }
      }
    }
  }
}

@Composable
fun FilterChipTab(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) GoldMetallic else MaterialTheme.colorScheme.background,
    modifier = Modifier.clickable { onClick() }
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}
