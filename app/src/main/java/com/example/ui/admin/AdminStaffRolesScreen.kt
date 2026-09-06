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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AdminUserEntity
import com.example.ui.FashionViewModel
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed

@Composable
fun AdminStaffRolesScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit
) {
  val staffUsers by viewModel.staffUsers.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentAdminUser.collectAsStateWithLifecycle()

  var showAddStaffDialog by remember { mutableStateOf(false) }

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
            text = "Staff & RBAC Permissions",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(staffUsers, key = { it.id }) { user ->
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
                color = if (user.role == "OWNER") GoldMetallic.copy(alpha = 0.2f) else Emerald700.copy(alpha = 0.1f),
                modifier = Modifier.size(42.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = if (user.role == "OWNER") Icons.Default.Security else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (user.role == "OWNER") GoldDark else Emerald700
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (user.role == "OWNER") GoldMetallic else Emerald700
                  ) {
                    Text(
                      text = user.role,
                      color = if (user.role == "OWNER") Emerald900 else Color.White,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
                Text(text = user.emailOrMobile, fontSize = 11.sp, color = Color.Gray)
                Text(
                  text = "Permissions: ${user.customPermissions}",
                  fontSize = 10.sp,
                  color = Color.DarkGray,
                  maxLines = 1
                )
              }

              // Toggle status (Owner cannot disable themselves)
              if (user.role != "OWNER") {
                Switch(
                  checked = user.isActive,
                  onCheckedChange = { viewModel.toggleStaffStatus(user) }
                )
              }
            }
          }
        }
      }
    }

    FloatingActionButton(
      onClick = { showAddStaffDialog = true },
      containerColor = GoldMetallic,
      contentColor = Emerald900,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Staff")
    }

    if (showAddStaffDialog) {
      AddStaffDialog(
        onDismiss = { showAddStaffDialog = false },
        onSave = { name, email, pass, role, perms ->
          viewModel.addStaff(
            name = name,
            emailOrMobile = email,
            password = pass,
            role = role,
            permissions = perms,
            onSuccess = { showAddStaffDialog = false },
            onError = { viewModel.showMessage(it) }
          )
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffDialog(
  onDismiss: () -> Unit,
  onSave: (name: String, email: String, pass: String, role: String, perms: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var emailOrMobile by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf("SALES_STAFF") }
  var roleExpanded by remember { mutableStateOf(false) }

  val availableRoles = listOf("MANAGER", "SALES_STAFF", "INVENTORY_MANAGER", "ACCOUNTANT", "CUSTOM")

  // Custom permissions
  var canViewProducts by remember { mutableStateOf(true) }
  var canEditProducts by remember { mutableStateOf(false) }
  var canManageOrders by remember { mutableStateOf(true) }
  var canIssueInvoices by remember { mutableStateOf(true) }
  var canAdjustStock by remember { mutableStateOf(false) }
  var canRecordExpenses by remember { mutableStateOf(false) }
  var canViewReports by remember { mutableStateOf(false) }

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
        Text("Create Staff Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald900)
        Text("Passwords are salted & hashed with SHA-256.", fontSize = 10.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Staff Full Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = emailOrMobile,
          onValueChange = { emailOrMobile = it },
          label = { Text("Login Email / Mobile *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Temporary Password *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Role Dropdown
        ExposedDropdownMenuBox(
          expanded = roleExpanded,
          onExpandedChange = { roleExpanded = !roleExpanded }
        ) {
          OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            readOnly = true,
            label = { Text("Assign Role") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = roleExpanded,
            onDismissRequest = { roleExpanded = false }
          ) {
            availableRoles.forEach { r ->
              DropdownMenuItem(
                text = { Text(r) },
                onClick = {
                  selectedRole = r
                  roleExpanded = false
                  // Auto-preset permissions
                  when (r) {
                    "MANAGER" -> {
                      canViewProducts = true
                      canEditProducts = true
                      canManageOrders = true
                      canIssueInvoices = true
                      canAdjustStock = true
                      canRecordExpenses = true
                      canViewReports = true
                    }
                    "SALES_STAFF" -> {
                      canViewProducts = true
                      canEditProducts = false
                      canManageOrders = true
                      canIssueInvoices = true
                      canAdjustStock = false
                      canRecordExpenses = false
                      canViewReports = false
                    }
                    "INVENTORY_MANAGER" -> {
                      canViewProducts = true
                      canEditProducts = true
                      canManageOrders = false
                      canIssueInvoices = false
                      canAdjustStock = true
                      canRecordExpenses = false
                      canViewReports = false
                    }
                    "ACCOUNTANT" -> {
                      canViewProducts = true
                      canEditProducts = false
                      canManageOrders = true
                      canIssueInvoices = true
                      canAdjustStock = false
                      canRecordExpenses = true
                      canViewReports = true
                    }
                    else -> {}
                  }
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Fine-grained Permissions Builder", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald900)

        PermCheckRow("View Products & Prices", canViewProducts) { canViewProducts = it }
        PermCheckRow("Edit Products & Change Price", canEditProducts) { canEditProducts = it }
        PermCheckRow("Manage Wholesale Enquiries", canManageOrders) { canManageOrders = it }
        PermCheckRow("Generate GST Invoices", canIssueInvoices) { canIssueInvoices = it }
        PermCheckRow("Stock In/Out Adjustments", canAdjustStock) { canAdjustStock = it }
        PermCheckRow("Record Expenses", canRecordExpenses) { canRecordExpenses = it }
        PermCheckRow("Access Reports & P/L", canViewReports) { canViewReports = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) { Text("Cancel") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (name.isBlank() || emailOrMobile.isBlank() || password.length < 6) return@Button
              val perms = listOfNotNull(
                if (canViewProducts) "PRODUCTS_VIEW" else null,
                if (canEditProducts) "PRODUCTS_EDIT" else null,
                if (canManageOrders) "ORDERS_MANAGE" else null,
                if (canIssueInvoices) "INVOICING" else null,
                if (canAdjustStock) "INVENTORY_ADJUST" else null,
                if (canRecordExpenses) "EXPENSES_RECORD" else null,
                if (canViewReports) "REPORTS_VIEW" else null
              ).joinToString(",")

              onSave(name.trim(), emailOrMobile.trim(), password, selectedRole, perms)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
          ) {
            Text("Create Account", color = GoldShimmer, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun PermCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 1.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
  }
}
