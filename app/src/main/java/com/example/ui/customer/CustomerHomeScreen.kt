package com.example.ui.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.FashionViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.ProductCard
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldShimmer
import com.example.ui.theme.SandParchment

@Composable
fun CustomerHomeScreen(
  viewModel: FashionViewModel,
  onOpenProduct: (Long) -> Unit,
  onViewAllCatalog: (category: String?) -> Unit
) {
  val context = LocalContext.current
  val trendingProducts by viewModel.trendingProducts.collectAsStateWithLifecycle()
  val newArrivals by viewModel.newArrivals.collectAsStateWithLifecycle()
  val featuredProducts by viewModel.featuredProducts.collectAsStateWithLifecycle()
  val discountedProducts by viewModel.discountedProducts.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val allProducts by viewModel.products.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Search Bar Header Section
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Emerald900)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.selectedCategoryFilter.value = "All"
              onViewAllCatalog(null)
            },
          shape = RoundedCornerShape(24.dp),
          color = Color.White
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = Emerald700,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Search abayas, dress materials, suits, fabrics...",
              color = Color.Gray,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    // 2. Hero Fashion Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.banner_fashion_hero),
            contentDescription = "Mahima Fashion Wholesale Collection",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          // Dark Gradient Overlay for high-contrast luxury readability
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Transparent,
                    Emerald900.copy(alpha = 0.85f),
                    Emerald900.copy(alpha = 0.95f)
                  )
                )
              )
          )

          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GoldMetallic
            ) {
              Text(
                text = "WHOLESALE DIRECT FROM MANUFACTURER",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = Emerald900,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Luxury Modest & Unstitched Collections",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Special bulk pricing for boutiques and retail shops across India",
              color = GoldShimmer.copy(alpha = 0.9f),
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { onViewAllCatalog(null) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Text(
                  text = "View Catalog",
                  color = Emerald900,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Button(
                onClick = {
                  val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=919327607195&text=Hello%20Mahima%20Fashion,%20I%20am%20interested%20in%20your%20wholesale%20clothing%20catalog.")
                  }
                  context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Chat,
                  contentDescription = "WhatsApp",
                  tint = GoldShimmer,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "WhatsApp Enquiry",
                  color = GoldShimmer,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // 3. Wholesale Highlights Banner
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Emerald800.copy(alpha = 0.08f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          WholesaleFeatureItem(Icons.Default.Verified, "GST Registered", "Verified B2B")
          WholesaleFeatureItem(Icons.Default.LocalShipping, "Pan India", "Fast Delivery")
          WholesaleFeatureItem(Icons.Default.CheckCircle, "Low MOQ", "5 to 10 pcs")
        }
      }
    }

    // 4. Popular Categories
    item {
      SectionHeader(
        title = "Popular Categories",
        actionText = "See All",
        onAction = { onViewAllCatalog(null) }
      )
    }

    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(categories) { category ->
          CategoryChip(
            category = category,
            onClick = { onViewAllCatalog(category.name) }
          )
        }
      }
    }

    // 5. Trending Collection
    if (trendingProducts.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(
          title = "Trending Wholesale Collection",
          actionText = "View All",
          onAction = {
            viewModel.selectedBadgeFilter.value = "Trending"
            onViewAllCatalog(null)
          }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(trendingProducts) { product ->
            Box(modifier = Modifier.width(200.dp)) {
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

    // 6. New Arrivals
    if (newArrivals.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(
          title = "New Arrivals",
          actionText = "View All",
          onAction = {
            viewModel.selectedBadgeFilter.value = "New Arrival"
            onViewAllCatalog(null)
          }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(newArrivals) { product ->
            Box(modifier = Modifier.width(200.dp)) {
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

    // 7. Featured Products
    if (featuredProducts.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(
          title = "Featured Boutiques Choice",
          actionText = "View All",
          onAction = {
            viewModel.selectedBadgeFilter.value = "Featured"
            onViewAllCatalog(null)
          }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(featuredProducts) { product ->
            Box(modifier = Modifier.width(200.dp)) {
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

    // 8. Wholesale Enquiry Contact Card
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald800),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Are You a Boutique or Shop Owner?",
            color = GoldLight,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Get customized wholesale quotes, GST invoices, and bulk lot dispatch directly to your city.",
            color = GoldShimmer.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "📍 2070, New Pashupati Textile Market, Opp. Shyam Market, Moti Begam Wadi, Ring Road, Surat, Gujarat",
            color = GoldLight,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
          Text(
            text = "📞 Mobile / WhatsApp: +91 9327607195",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:+919327607195")
                }
                context.startActivity(intent)
              },
              colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Call",
                tint = Emerald900,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Call Us", color = Emerald900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
              onClick = {
                viewModel.navigateTo(ScreenDestination.CustomerContact)
              },
              colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Contact Info", color = GoldShimmer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  actionText: String,
  onAction: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = Emerald800
    )
    Row(
      modifier = Modifier.clickable { onAction() },
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = actionText,
        style = MaterialTheme.typography.labelMedium,
        color = GoldDark,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.width(2.dp))
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Navigate",
        tint = GoldDark,
        modifier = Modifier.size(14.dp)
      )
    }
  }
}

@Composable
fun CategoryChip(category: CategoryEntity, onClick: () -> Unit) {
  Surface(
    modifier = Modifier.clickable { onClick() },
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = category.name,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Emerald800
      )
      if (category.description.isNotEmpty()) {
        Text(
          text = category.description,
          fontSize = 10.sp,
          color = Color.Gray,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun WholesaleFeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = Emerald700,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Emerald900)
      Text(text = subtitle, fontSize = 9.sp, color = Color.Gray)
    }
  }
}
