package com.example.giaodoan.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import coil.compose.AsyncImage
import com.example.giaodoan.R
import com.example.giaodoan.model.Cart
import com.example.giaodoan.model.Category
import com.example.giaodoan.model.Product
import com.example.giaodoan.model.SessionManager

@Composable
fun formatPrice(price: Long): String {
    return price.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

@Composable
fun TopSearchBar(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(59.dp)
            .background(Color(0xFFFF5722))
    ) {
        // Search View
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .width(365.dp - 48.dp)
                .height(59.dp)

        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(
                        text = "Tìm kiếm...",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.seach),
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.human),
            contentDescription = stringResource(id = R.string.human),
            tint = Color.White,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clickable{
                    navController.navigate("profile")
                }
        )
    }
}

@Composable
fun DanhSachMonAn(navController: NavController) {
    Scaffold(
        topBar = {
            TopSearchBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        DanhSachMonAnContent(paddingValues)
    }
}

@Composable
fun DanhSachMonAnContent(paddingValues: PaddingValues) {
    val context = LocalContext.current

    val categoryModel = Category(context)
    val isSuccessful = categoryModel.Select_category()
    isSuccessful.map {
        CategoryDisplay(
            name = it.category_name.toString(),
            description = it.getDescription(),
            id =it.getCategory_id()
        )
    }

    val productModel = Product(context)
    val productList = productModel.Select_product().map {
        ProductDisplay(
            product_id = it.product_id,
            category_id = it.category_id,
            product_name = it.product_name,
            price = it.price,
            img = it.img,
            category_name = it.category_name
        )
    }

    val danhSachDanhMuc = productList.map { it.category_name }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(paddingValues),
    ) {
        Text(
            text = "Thực đơn",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        LazyColumn {
            danhSachDanhMuc.forEach { category ->
                item {
                    Text(
                        text = category.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                val monAnTheoDanhMuc = productList.filter { it.category_name == category }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(220.dp)
                    ) {
                        items(monAnTheoDanhMuc) { product ->
                            ItemMonAn(product = product, onClick = {  })
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ItemMonAn(product: ProductDisplay, onClick: () -> Unit) {
    val imageUri = remember(product.img) {
        if (product.img.isNotEmpty()) {
            try {
                Uri.parse(product.img)
            } catch (e: Exception) {
                Log.e("ProductItem", "Error parsing URI: ${e.message}")
                null
            }
        } else null
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .width(200.dp)
            .height(200.dp)
            .background(Color.White)
            .padding(8.dp)
            .clickable{
                val addcart = Cart(context)
                if(addcart.Add_cart(SessionManager.getInstance(context).getUserId(),product.product_id,product.category_id)){
                    Toast.makeText(context, "Đã thêmm sản phẩm vào giỏ hàng", Toast.LENGTH_LONG).show()

                }

            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = product.product_name,
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Text(
            text = product.product_name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier
        )
        Text(
            text = "${formatPrice(product.price)}đ",
            color = Color(0xFF4CAF50),
            modifier = Modifier
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(Color(0xFFFF5722))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 40.dp)
                .weight(1f),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = stringResource(id = R.string.chat),
                modifier = Modifier.size(40.dp)

            )
            Text(
                text = stringResource(id = R.string.chat),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f),

            ) {
            Icon(
                painter = painterResource(id = R.drawable.food),
                contentDescription = stringResource(id = R.string.food),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(id = R.string.food),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate("cart")
                },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = stringResource(id = R.string.cart),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(id = R.string.cart),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

