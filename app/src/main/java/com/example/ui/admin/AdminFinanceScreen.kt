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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseEntity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.SandParchment
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFinanceScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()
  val payments by viewModel.payments.collectAsStateWithLifecycle()
  val expenses by viewModel.expenses.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = P&L Overview, 1 = Expenses, 2 = Customer Payments
  var showAddExpenseDialog by remember { mutableStateOf(false) }

  val totalRevenue = invoices.sumOf { it.totalAmount }
  val totalPaid = payments.sumOf { it.amount } + invoices.sumOf { it.amountPaid }
  val totalExpenses = expenses.sumOf { it.amount }
  val netProfit = totalRevenue - totalExpenses

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
            text = "Finance & Expense Management",
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
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("P & L Summary", fontWeight = FontWeight.Bold) })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Expenses (${expenses.size})", fontWeight = FontWeight.Bold) })
        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Payments (${payments.size})", fontWeight = FontWeight.Bold) })
      }

      if (selectedTab == 0) {
        // P&L Summary
        Column(modifier = Modifier.padding(16.dp)) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Financial Performance Snapshot", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
              Spacer(modifier = Modifier.height(10.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Wholesale Invoiced", fontSize = 12.sp)
                Text("₹${totalRevenue.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald700)
              }
              Spacer(modifier = Modifier.height(6.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Operational & Mill Expenses", fontSize = 12.sp)
                Text("₹${totalExpenses.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StockRed)
              }
              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider()
              Spacer(modifier = Modifier.height(10.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estimated Net Profit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald900)
                Text(
                  text = "₹${netProfit.toInt()}",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (netProfit >= 0) StockGreen else StockRed
                )
              }
            }
          }
        }
      } else if (selectedTab == 1) {
        // Expenses list
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(expenses, key = { it.id }) { exp ->
            val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = exp.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                  Text(text = exp.description, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  Text(text = "Recorded on: ${df.format(Date(exp.date))}", fontSize = 10.sp, color = Color.Gray)
                }
                Text(text = "₹${exp.amount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = StockRed)
              }
            }
          }
        }
      } else {
        // Payments List
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(payments, key = { it.id }) { pay ->
            val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Inv: ${pay.invoiceNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                  Text(text = pay.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  Text(text = "Mode: ${pay.paymentMethod} • ${df.format(Date(pay.date))}", fontSize = 10.sp, color = Color.Gray)
                }
                Text(text = "+₹${pay.amount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = StockGreen)
              }
            }
          }
        }
      }
    }

    if (selectedTab == 1) {
      FloatingActionButton(
        onClick = { showAddExpenseDialog = true },
        containerColor = GoldMetallic,
        contentColor = Emerald900,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
      }
    }

    if (showAddExpenseDialog) {
      AddExpenseDialog(
        onDismiss = { showAddExpenseDialog = false },
        onSave = { expense ->
          viewModel.recordExpense(expense) {
            showAddExpenseDialog = false
          }
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
  onDismiss: () -> Unit,
  onSave: (ExpenseEntity) -> Unit
) {
  var category by remember { mutableStateOf("Transportation & Freight") }
  var amountText by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  val categories = listOf(
    "Transportation & Freight",
    "Textile Mill Raw Material",
    "Shop Rent & Utilities",
    "Packing Materials & Poly Bags",
    "Staff Salary & Commission",
    "Marketing & Sample Catalog",
    "Miscellaneous"
  )
  var catExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Record Business Expense") },
    text = {
      Column {
        ExposedDropdownMenuBox(
          expanded = catExpanded,
          onExpandedChange = { catExpanded = !catExpanded }
        ) {
          OutlinedTextField(
            value = category,
            onValueChange = {},
            readOnly = true,
            label = { Text("Expense Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { c ->
              DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Amount (₹) *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description / Remarks *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt > 0 && description.isNotBlank()) {
            onSave(
              ExpenseEntity(
                category = category,
                amount = amt,
                description = description.trim()
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
      ) {
        Text("Record Expense", color = GoldShimmer)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
