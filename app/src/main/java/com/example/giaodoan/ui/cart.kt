package com.example.giaodoan.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.giaodoan.R
import com.example.giaodoan.model.Cart
import com.example.giaodoan.model.SessionManager

data class CartItem(
    val cart_id: Int,
    val product_id: Int,
    val category_id: Int,
    val category_name: String,
    val product_name: String,
    val price: Long,
    var total_amount: Int
)

@Composable
fun GioHangScreenByCategory() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val cartModel = remember { Cart(context) }

    // Lấy userId từ SessionManager
    val userId = sessionManager.getUserId()
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    val loadCartData = {
        cartItems = cartModel.Select_cart(userId).map {
            CartItem(
                cart_id = it.getCart_id(),
                product_id = it.getProduct_id(),
                category_id = it.getCategory_id(),
                category_name = it.getCategory_name(),
                product_name = it.getProduct_name(),
                price = it.getPrice(),
                total_amount = it.getTotal_amount()
            )
        }
    }
    loadCartData()
    // Tính tổng tiền của giỏ hàng
    val totalPrice = remember(cartItems) {
        cartItems.sumOf { it.price * it.total_amount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffffff))
            .padding(16.dp)
    ) {
        // Tiêu đề giỏ hàng
        Text(
            text = "Giỏ hàng",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        if (cartItems.isEmpty()) {
            // Hiển thị thông báo khi giỏ hàng trống
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Giỏ hàng của bạn đang trống",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                val categories = cartItems.map { it.category_name }.distinct()


                categories.forEach { category ->
                    item {
                        CategoryHeader(category)
                    }

                    val itemsInCategory = cartItems.filter { it.category_name == category }
                    items(itemsInCategory) { item ->
                        CartItemRow(item = item,loadCartData)
                    }

                    item {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp
                        )
                    }
                }
            }

            Text(
                text = "Tổng tiền: ${formatPrice(totalPrice)}đ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { /* Xử lý đặt hàng */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Đặt hàng",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun CategoryHeader(categoryName: String) {
    Text(
        text = categoryName.uppercase(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

@Composable
fun CartItemRow(item: CartItem, loadCartData: () -> Unit) {
    var textValue by remember { mutableStateOf(item.total_amount.toString()) }
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.product_name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(30.dp),
            ){
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight()
                        .clickable{
                            val reduce_cart = Cart(context)

                            if(reduce_cart.reduce_cart(item.cart_id)){
                                textValue = (textValue.toInt() -1).toString()
                                loadCartData()
                            }
                        }
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    text = "-",
                    fontSize = 24.sp,
                )

                BasicTextField(
                    value = textValue,
                    onValueChange = { newValue: String ->
                        textValue = newValue
                        if (!newValue.isEmpty()){
                            val newIntValue = newValue.toIntOrNull() ?: -1
                            if(newIntValue > 0){
                                if(Cart(context).update_cart(item.cart_id, newIntValue)){
                                    loadCartData()
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    ),
                    singleLine = true
                )

                Text(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight()
                        .clickable{
                            val add_cart = Cart(context)
                            if(add_cart.Add_cart(SessionManager.getInstance(context).getUserId(),item.product_id, item.category_id)){
                                textValue = (textValue.toInt() +1).toString()
                                loadCartData()
                            }
                        }
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = "+",

                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                )
            }
        }


    }
}


fun formatPrice(price: String): String {
    val priceStr = price.toString()
    if (priceStr.length < 4) {
        return priceStr
    }
    val result = StringBuilder()
    for (i in priceStr.length - 1 downTo 0) {
        result.insert(0, priceStr[i])
        if (i > 0 && (priceStr.length - i) % 3 == 0) {
            result.insert(0, ".")
        }
    }
    return result.toString()
}