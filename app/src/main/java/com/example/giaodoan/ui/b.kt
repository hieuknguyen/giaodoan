package com.example.giaodoan.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.giaodoan.R
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.giaodoan.database.api.ApiService
import com.example.giaodoan.model.Cart
import com.example.giaodoan.model.SessionManager
import com.example.giaodoan.model.User
import com.example.giaodoan.model.purchased_product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PaymentOptionScreen(totalPrice: String, context: Context, navController: NavController) {
    val sessionManager = remember { SessionManager.getInstance(context) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var showQrDialog by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Khởi tạo Retrofit
    val client = OkHttpClient.Builder().build()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://img.vietqr.io/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(ApiService::class.java)

    // Hàm để tải ảnh QR code
    fun loadQrImage() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val bankId = "970422" // Mã ngân hàng (VCB)
                val accountNumber = "0344466721"
                val amount = totalPrice // Số tiền mặc định
                val description = URLEncoder.encode("Thanh toan don hang", StandardCharsets.UTF_8.toString())
                val accountName = URLEncoder.encode("NGUYEN TRUNG HIEU", StandardCharsets.UTF_8.toString())
                val format = "compact2"

                val response = apiService.getQrImage(
                    bankId = bankId,
                    accountNumber = accountNumber,
                    format = format,
                    amount = amount,
                    additionalInfo = description,
                    accountName = accountName
                )

                if (response.isSuccessful) {
                    qrBitmap = withContext(Dispatchers.IO) {
                        response.body()?.byteStream()?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    }

                    if (qrBitmap == null) {
                        errorMessage = "Không thể xử lý ảnh QR"
                    }
                } else {
                    errorMessage = "Lỗi: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Phương thức thanh toán",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Chọn phương thức thanh toán",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Payment Options
            PaymentMethodCard(
                paymentMethod = PaymentMethod.COD,
                isSelected = selectedPaymentMethod == PaymentMethod.COD,
                onSelect = { selectedPaymentMethod = PaymentMethod.COD }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodCard(
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                isSelected = selectedPaymentMethod == PaymentMethod.BANK_TRANSFER,
                onSelect = { selectedPaymentMethod = PaymentMethod.BANK_TRANSFER }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = {
                    selectedPaymentMethod?.let {
                        if (it == PaymentMethod.BANK_TRANSFER) {
                            loadQrImage() // Tải ảnh QR khi người dùng chọn chuyển khoản
                            showQrDialog = true
                        }else{
                            val user_id = sessionManager.userId
                            val cart = Cart(context)
                            val loadCartData = {
                                cartItems = cart.Select_cart(user_id).map {
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
                            cartItems.forEach {
                                purchased_product(context).addPurchasedProduct(it.product_id,user_id,it.product_name,it.total_amount,it.price)
                                cart.Delete_cart(it.cart_id)
                            }


                            Toast.makeText(context, "Thanh Toán thành công", Toast.LENGTH_SHORT).show()
                            navController.navigate("rate") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedPaymentMethod != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xác nhận", fontSize = 16.sp)
            }
        }

        // QR Code Dialog
        if (showQrDialog) {
            Dialog(onDismissRequest = { showQrDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Quét mã QR để thanh toán",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isLoading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                errorMessage != null -> {
                                    Text(
                                        text = errorMessage ?: "Lỗi không xác định",
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                qrBitmap != null -> {
                                    Image(
                                        bitmap = qrBitmap!!.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier.fillMaxSize(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showQrDialog = false
                                val user_id = sessionManager.userId
                                val cart = Cart(context)
                                val loadCartData = {
                                    cartItems = cart.Select_cart(user_id).map {
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
                                cartItems.forEach {
                                    purchased_product(context).addPurchasedProduct(it.product_id,user_id,it.product_name,it.total_amount,it.price)
                                    cart.Delete_cart(it.cart_id)
                                }


                                Toast.makeText(context, "Thanh Toán thành công", Toast.LENGTH_SHORT).show()
                                navController.navigate("rate") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }

                                      },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đã thanh toán")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = paymentMethod.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = paymentMethod.iconResId),
                contentDescription = paymentMethod.title,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

enum class PaymentMethod(
    val title: String,
    val description: String,
    val iconResId: Int
) {
    COD(
        title = "Thanh toán khi nhận hàng",
        description = "Thanh toán bằng tiền mặt khi nhận được hàng",
        iconResId = R.drawable.cod
    ),
    BANK_TRANSFER(
        title = "Chuyển khoản ngân hàng",
        description = "Chuyển khoản qua tài khoản ngân hàng",
        iconResId = R.drawable.bank
    )
}