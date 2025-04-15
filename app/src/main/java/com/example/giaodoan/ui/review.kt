package com.example.giaodoan.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.giaodoan.R
import com.example.giaodoan.model.SessionManager
import com.example.giaodoan.model.purchased_product

import androidx.compose.material.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun review(){
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chưa đánh giá", "Đã đánh giá")
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Đánh giá của tôi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Tab bar
        TabRow(
            selectedTabIndex = selectedTab,
            backgroundColor = Color.White,
            contentColor = Color(0xFF1976D2)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        // Tab content
        when (selectedTab) {
            0 -> not_rate(context)
            1 -> Rated(context)
        }
    }
}
@Composable
fun Rated(context: Context) {
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.userId
    val products = remember { purchased_product(context).Select_product(userId) ?: emptyList() }

    // Filter products that have reviews
    val filtered = products.filter { it?.has_review == 1 }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(filtered.size) { index ->
            // Access the item safely using index
            var rating by remember { mutableStateOf(0) }
            var review by remember { mutableStateOf("") }

            val item = filtered[index]
            item?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(text = "Tên sản phẩm: " +it.product_name, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF4CD373)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "số lượng: " +it.total_amount, fontWeight = FontWeight.Bold)
                            Text(text = "giá tiền: " +it.price, fontWeight = FontWeight.Bold)
                        }
                        Text(("review:" + it.review?.toString()) ?: "")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { starIndex ->
                                if(starIndex < it.rating){
                                    Icon(
                                        imageVector = Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier
                                            .size(24.dp)

                                    )
                                }
                                else{
                                    Icon(
                                        painter = painterResource(id = R.drawable.star),
                                        contentDescription = null,
                                        tint = if (starIndex < rating) Color(0xFFFFD700) else Color.Gray,
                                        modifier = Modifier
                                            .size(24.dp)

                                    )
                                }

                            }

                        }


                    }
                }
            }
        }
    }
}


@Composable
fun not_rate(context: Context) {
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.userId

    // Using mutableStateOf for products to allow reloading
    var products by remember { mutableStateOf(purchased_product(context).Select_product(userId) ?: emptyList()) }

    // Using mutableStateOf for filtered list to properly update UI
    var filtered by remember { mutableStateOf(products.filter { it?.has_review == 0 }) }

    // Function to reload products and update filtered list
    val reloadProducts = {
        products = purchased_product(context).Select_product(userId) ?: emptyList()
        filtered = products.filter { it?.has_review == 0 }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(filtered.size) { index ->
            if (index < filtered.size) {  // Safety check
                var rating by remember { mutableStateOf(0) }
                var review by remember { mutableStateOf("") }

                val item = filtered[index]
                item?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = "Tên sản phẩm: " + it.product_name, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF4CD373)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "số lượng: " + it.total_amount, fontWeight = FontWeight.Bold)
                                Text(text = "giá tiền: " + it.price, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(
                                value = review,
                                onValueChange = { review = it },
                                label = { Text("review") },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 16.sp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Gray
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { starIndex ->
                                    if (starIndex < rating) {
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable {
                                                    rating = starIndex + 1
                                                    Log.d("Rating", "User selected $rating stars")
                                                }
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.star),
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable {
                                                    rating = starIndex + 1
                                                }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (purchased_product(context).Update_purchased_product(userId, it.purchased_products_id, review, rating)) {
                                            Toast.makeText(context, "đánh giá thành công!", Toast.LENGTH_SHORT).show()
                                            // Reload the products list after successful rating
                                            reloadProducts()
                                        } else {
                                            Toast.makeText(context, "đánh giá thất bại!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2))
                                ) {
                                    Text(
                                        color = Color.White,
                                        text = "Đánh giá"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class products_purchasd(
    val purchased_products_id : Int,
    val product_id: Int,
    val user_id: Int,
    val product_name: String,
    val total_amount: Int,
    val price : Long,
    val rating: Int,
    val review : String,
    val has_review: Int,
)