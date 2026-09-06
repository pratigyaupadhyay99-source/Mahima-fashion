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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockRed

@Composable
fun AdminCategoriesScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var newCatName by remember { mutableStateOf("") }
  var newCatDesc by remember { mutableStateOf("") }
  var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

  Box(modifier = Modifier.fillMaxSize()) {
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
            text = "Categories & Classifications",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(categories, key = { it.id }) { cat ->
          val count = products.count { it.category.equals(cat.name, ignoreCase = true) }
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Emerald700.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = Emerald700)
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(text = cat.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                if (cat.description.isNotEmpty()) {
                  Text(text = cat.description, fontSize = 11.sp, color = Color.Gray)
                }
                Text(
                  text = "$count Products in Catalog",
                  fontSize = 11.sp,
                  color = GoldDark,
                  fontWeight = FontWeight.SemiBold
                )
              }
              IconButton(onClick = { categoryToDelete = cat }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StockRed.copy(alpha = 0.8f))
              }
            }
          }
        }
      }
    }

    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = GoldMetallic,
      contentColor = Emerald900,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Category")
    }

    if (showAddDialog) {
      AlertDialog(
        onDismissRequest = { showAddDialog = false },
        title = { Text("Add Wholesale Category") },
        text = {
          Column {
            OutlinedTextField(
              value = newCatName,
              onValueChange = { newCatName = it },
              label = { Text("Category Name *") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = newCatDesc,
              onValueChange = { newCatDesc = it },
              label = { Text("Description") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newCatName.isNotBlank()) {
                viewModel.addCategory(CategoryEntity(name = newCatName.trim(), description = newCatDesc.trim())) {
                  newCatName = ""
                  newCatDesc = ""
                  showAddDialog = false
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
          ) {
            Text("Add", color = GoldShimmer)
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
        }
      )
    }

    if (categoryToDelete != null) {
      AlertDialog(
        onDismissRequest = { categoryToDelete = null },
        title = { Text("Delete Category?") },
        text = { Text("Are you sure you want to delete '${categoryToDelete?.name}'?") },
        confirmButton = {
          Button(
            onClick = {
              categoryToDelete?.let { viewModel.deleteCategory(it) }
              categoryToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = StockRed)
          ) {
            Text("Delete", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { categoryToDelete = null }) { Text("Cancel") }
        }
      )
    }
  }
}
