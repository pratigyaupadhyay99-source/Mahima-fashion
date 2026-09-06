package com.example.ui.admin

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CustomerEntity
import com.example.data.model.SupplierEntity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange

@Composable
fun AdminCustomersSuppliersScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Customers / Boutiques, 1 = Suppliers / Mills
  var showAddCustomerDialog by remember { mutableStateOf(false) }
  var showAddSupplierDialog by remember { mutableStateOf(false) }

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
            text = "Customers & Textile Suppliers",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Emerald700
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Boutiques & Clients (${customers.size})", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Textile Mills (${suppliers.size})", fontWeight = FontWeight.Bold) }
        )
      }

      if (selectedTab == 0) {
        // Customers List
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(customers, key = { it.id }) { cust ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(cust.businessName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                    Text("Contact: ${cust.name}", fontSize = 11.sp, color = Color.Gray)
                  }
                  if (cust.outstandingAmount > 0) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = StockOrange.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "DUE: ₹${cust.outstandingAmount.toInt()}",
                        color = StockOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Purchases: ₹${cust.totalPurchases.toInt()} | Paid: ₹${cust.amountPaid.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Emerald700)
                if (cust.gstNumber.isNotEmpty()) Text("GSTIN: ${cust.gstNumber}", fontSize = 10.sp, color = Color.Gray)
                if (cust.address.isNotEmpty()) Text("Location: ${cust.address}", fontSize = 10.sp, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                    onClick = {
                      val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${cust.mobile}") }
                      context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                  ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 11.sp, color = GoldShimmer)
                  }
                  Button(
                    onClick = {
                      val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://api.whatsapp.com/send?phone=${cust.mobile.replace("+", "").replace(" ", "")}")
                      }
                      context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic)
                  ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = Emerald900)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp, color = Emerald900, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      } else {
        // Suppliers List
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(suppliers, key = { it.id }) { sup ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(sup.company, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                Text("Contact Person: ${sup.name}", fontSize = 11.sp, color = Color.Gray)
                Text("Materials: ${sup.productsSupplied}", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.SemiBold)
                if (sup.amountPayable > 0) {
                  Text("Payable: ₹${sup.amountPayable.toInt()}", fontSize = 11.sp, color = StockOrange, fontWeight = FontWeight.Bold)
                }
                if (sup.gstNumber.isNotEmpty()) Text("GSTIN: ${sup.gstNumber}", fontSize = 10.sp, color = Color.Gray)
                if (sup.address.isNotEmpty()) Text("Mill Address: ${sup.address}", fontSize = 10.sp, color = Color.DarkGray)
              }
            }
          }
        }
      }
    }

    FloatingActionButton(
      onClick = {
        if (selectedTab == 0) showAddCustomerDialog = true else showAddSupplierDialog = true
      },
      containerColor = GoldMetallic,
      contentColor = Emerald900,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add")
    }

    // Add Customer Dialog
    if (showAddCustomerDialog) {
      var name by remember { mutableStateOf("") }
      var bName by remember { mutableStateOf("") }
      var mobile by remember { mutableStateOf("") }
      var address by remember { mutableStateOf("") }
      var gst by remember { mutableStateOf("") }

      AlertDialog(
        onDismissRequest = { showAddCustomerDialog = false },
        title = { Text("Add Boutique / Customer") },
        text = {
          Column {
            OutlinedTextField(value = bName, onValueChange = { bName = it }, label = { Text("Boutique / Shop Name *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Owner Contact Name *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile / WhatsApp *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("City / Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = gst, onValueChange = { gst = it }, label = { Text("GSTIN") }, modifier = Modifier.fillMaxWidth())
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (bName.isNotBlank() && mobile.isNotBlank()) {
                // Save customer via viewModel
                // Simple helper
                showAddCustomerDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
          ) {
            Text("Save", color = GoldShimmer)
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddCustomerDialog = false }) { Text("Cancel") }
        }
      )
    }
  }
}
