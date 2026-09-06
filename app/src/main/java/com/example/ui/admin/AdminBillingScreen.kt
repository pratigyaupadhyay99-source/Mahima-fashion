package com.example.ui.admin

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.InvoiceEntity
import com.example.data.model.ProductEntity
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

data class InvoiceDraftItem(
  val product: ProductEntity,
  var quantity: Int,
  var unitPrice: Double
)

@Composable
fun AdminBillingScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()

  var showCreateInvoiceDialog by remember { mutableStateOf(false) }
  var invoiceToView by remember { mutableStateOf<InvoiceEntity?>(null) }

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
            text = "Sales & Wholesale Invoicing",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Summary Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Total Billed", fontSize = 11.sp, color = Color.Gray)
            Text("₹${invoices.sumOf { it.totalAmount }.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald700)
          }
          Column {
            Text("Amount Received", fontSize = 11.sp, color = Color.Gray)
            Text("₹${invoices.sumOf { it.amountPaid }.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StockGreen)
          }
          Column {
            Text("Outstanding", fontSize = 11.sp, color = Color.Gray)
            Text("₹${invoices.sumOf { it.balanceDue }.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StockOrange)
          }
        }
      }

      if (invoices.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No invoices generated yet. Click + to create a wholesale bill.", color = Color.Gray, fontSize = 13.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(invoices, key = { it.id }) { inv ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { invoiceToView = inv },
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
                  Text(text = inv.invoiceNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (inv.balanceDue <= 0) StockGreen.copy(alpha = 0.15f) else StockOrange.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = if (inv.balanceDue <= 0) "PAID" else "BAL DUE: ₹${inv.balanceDue.toInt()}",
                      color = if (inv.balanceDue <= 0) StockGreen else StockOrange,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${inv.customerName} (${inv.businessName})", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "Total: ₹${inv.totalAmount.toInt()} (Paid: ₹${inv.amountPaid.toInt()})", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.SemiBold)
                  val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                  Text(text = df.format(Date(inv.date)), fontSize = 10.sp, color = Color.Gray)
                }
              }
            }
          }
        }
      }
    }

    FloatingActionButton(
      onClick = { showCreateInvoiceDialog = true },
      containerColor = GoldMetallic,
      contentColor = Emerald900,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create Invoice")
    }

    if (showCreateInvoiceDialog) {
      CreateInvoiceDialog(
        products = products,
        onDismiss = { showCreateInvoiceDialog = false },
        onSave = { invoice, itemsToDeduct ->
          viewModel.createInvoice(invoice) {
            // Deduct stock
            itemsToDeduct.forEach { (prodId, qty) ->
              viewModel.adjustStock(prodId, -qty, "Wholesale Invoice #${invoice.invoiceNumber}")
            }
            showCreateInvoiceDialog = false
          }
        }
      )
    }

    if (invoiceToView != null) {
      PrintableInvoiceDialog(invoice = invoiceToView!!, onDismiss = { invoiceToView = null })
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceDialog(
  products: List<ProductEntity>,
  onDismiss: () -> Unit,
  onSave: (InvoiceEntity, List<Pair<Long, Int>>) -> Unit
) {
  var customerName by remember { mutableStateOf("") }
  var businessName by remember { mutableStateOf("") }
  var mobile by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var gstin by remember { mutableStateOf("") }

  val draftItems = remember { mutableStateListOf<InvoiceDraftItem>() }
  var selectedProduct by remember { mutableStateOf<ProductEntity?>(products.firstOrNull()) }
  var productDropdownExpanded by remember { mutableStateOf(false) }

  var discountAmountText by remember { mutableStateOf("0") }
  var amountPaidText by remember { mutableStateOf("0") }
  var paymentMethod by remember { mutableStateOf("Bank Transfer / NEFT") }

  val subtotal = draftItems.sumOf { it.unitPrice * it.quantity }
  val gstAmount = subtotal * 0.05 // 5% GST for fabrics & apparel
  val discount = discountAmountText.toDoubleOrNull() ?: 0.0
  val finalTotal = (subtotal + gstAmount - discount).coerceAtLeast(0.0)

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        Text("Generate Wholesale Invoice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Preserves historical prices automatically.", fontSize = 10.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Customer / Owner Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = businessName,
          onValueChange = { businessName = it },
          label = { Text("Boutique / Shop Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Mobile *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = gstin,
            onValueChange = { gstin = it },
            label = { Text("GSTIN") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))

        Text("Select Wholesale Products", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)

        ExposedDropdownMenuBox(
          expanded = productDropdownExpanded,
          onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
        ) {
          OutlinedTextField(
            value = selectedProduct?.let { "${it.name} (Rate: ₹${it.wholesalePrice.toInt()})" } ?: "Select",
            onValueChange = {},
            readOnly = true,
            label = { Text("Product") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = productDropdownExpanded,
            onDismissRequest = { productDropdownExpanded = false }
          ) {
            products.forEach { p ->
              DropdownMenuItem(
                text = { Text("${p.name} (${p.productCode}) - ₹${p.wholesalePrice.toInt()}") },
                onClick = {
                  selectedProduct = p
                  productDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            selectedProduct?.let { p ->
              draftItems.add(InvoiceDraftItem(p, p.minimumOrderQuantity, p.wholesalePrice))
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add Selected Product to Invoice")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Added items table
        draftItems.forEachIndexed { index, item ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = SandParchment,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = item.product.name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                  text = "${item.quantity} pcs @ ₹${item.unitPrice.toInt()} = ₹${(item.quantity * item.unitPrice).toInt()}",
                  fontSize = 10.sp,
                  color = Emerald700
                )
              }
              IconButton(onClick = { draftItems.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = StockRed, modifier = Modifier.size(16.dp))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Calculations
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Emerald900.copy(alpha = 0.06f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Subtotal", fontSize = 11.sp)
              Text("₹${subtotal.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("GST (5%)", fontSize = 11.sp)
              Text("₹${gstAmount.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Grand Total", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
              Text("₹${finalTotal.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Emerald700)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = amountPaidText,
            onValueChange = { amountPaidText = it },
            label = { Text("Amount Paid (₹)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = paymentMethod,
            onValueChange = { paymentMethod = it },
            label = { Text("Payment Mode") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) { Text("Cancel") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (customerName.isBlank() || draftItems.isEmpty()) return@Button
              val invNum = "MF-INV-${System.currentTimeMillis() % 100000}"
              val paid = amountPaidText.toDoubleOrNull() ?: 0.0
              val balance = (finalTotal - paid).coerceAtLeast(0.0)

              val itemsJson = draftItems.joinToString(";") {
                "${it.product.name}#${it.product.productCode}#${it.quantity}#${it.unitPrice}#${it.quantity * it.unitPrice}"
              }

              val invoice = InvoiceEntity(
                invoiceNumber = invNum,
                customerName = customerName.trim(),
                businessName = businessName.trim(),
                customerMobile = mobile.trim(),
                itemsJson = itemsJson,
                subtotal = subtotal,
                discount = discount,
                gstAmount = gstAmount,
                totalAmount = finalTotal,
                amountPaid = paid,
                balanceDue = balance,
                paymentMethod = paymentMethod,
                paymentStatus = if (balance <= 0) "Paid" else if (paid > 0) "Partially Paid" else "Pending"
              )

              val deductions = draftItems.map { it.product.id to it.quantity }
              onSave(invoice, deductions)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
          ) {
            Text("Save & Issue Invoice", color = GoldShimmer, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun PrintableInvoiceDialog(
  invoice: InvoiceEntity,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        // Invoice Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column {
            Text("MAHIMA FASHION", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald900)
            Text("Wholesale Modest Clothing & Fabrics", fontSize = 10.sp, color = GoldDark)
            Text("2070, New Pashupati Textile Market, Opp. Shyam Market, Moti Begam Wadi, Ring Road, Surat, Gujarat", fontSize = 8.sp, color = Color.DarkGray)
            Text("Mobile / Msg: +91 9327607195 | GSTIN: 24AAACM1234F1Z8", fontSize = 9.sp, color = Color.Gray)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("TAX INVOICE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald700)
            Text(invoice.invoiceNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            Text(df.format(Date(invoice.date)), fontSize = 10.sp, color = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))

        Text("Billed To:", fontSize = 10.sp, color = Color.Gray)
        Text("${invoice.customerName} (${invoice.businessName})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        if (invoice.customerMobile.isNotEmpty()) Text("Mobile: ${invoice.customerMobile}", fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Itemized Table
        Surface(color = SandParchment, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
          Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Item & SKU", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text("Qty", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
            Text("Rate", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Total", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
          }
        }

        invoice.itemsJson.split(";").filter { it.isNotBlank() }.forEach { line ->
          val parts = line.split("#")
          if (parts.size >= 5) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${parts[0]} (${parts[1]})", fontSize = 10.sp, modifier = Modifier.weight(2f), maxLines = 1)
              Text(parts[2], fontSize = 10.sp, modifier = Modifier.weight(0.7f))
              Text("₹${parts[3].toDoubleOrNull()?.toInt() ?: parts[3]}", fontSize = 10.sp, modifier = Modifier.weight(1f))
              Text("₹${parts[4].toDoubleOrNull()?.toInt() ?: parts[4]}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Subtotal", fontSize = 11.sp)
          Text("₹${invoice.subtotal.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("GST (5%)", fontSize = 11.sp)
          Text("₹${invoice.gstAmount.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Grand Total", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald900)
          Text("₹${invoice.totalAmount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Emerald700)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Amount Paid (${invoice.paymentMethod})", fontSize = 11.sp, color = StockGreen)
          Text("₹${invoice.amountPaid.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StockGreen)
        }
        if (invoice.balanceDue > 0) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Balance Due", fontSize = 12.sp, color = StockOrange, fontWeight = FontWeight.Bold)
            Text("₹${invoice.balanceDue.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StockOrange)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Close Invoice", color = GoldShimmer, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
