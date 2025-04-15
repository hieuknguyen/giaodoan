package com.example.giaodoan.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.giaodoan.model.Cart
import com.example.giaodoan.model.SessionManager
import kotlin.random.Random
import androidx.compose.foundation.border
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Composable
fun ManHinhChiTietMonAn(navController: NavController, product: ProductDisplay) {
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
    val scrollState = rememberScrollState()
    var quantity by remember { mutableStateOf(1) }
    val ratings = remember { Random.nextInt(10, 100) }
    val ratingScore = remember { 4.0f + Random.nextFloat() * 1.0f }

    // Màu chủ đạo
    val primaryColor = Color(0xFFFF5722)
    val secondaryColor = Color(0xFF4CAF50)
    val backgroundColor = Color(0xFFF8F8F8)
    val surfaceColor = Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        // Background blur image
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(15.dp)
                    .alpha(0.3f),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = product.product_name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Chức năng yêu thích */ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Yêu thích"
                            )
                        }
                        IconButton(onClick = { /* Chức năng chia sẻ */ }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Chia sẻ"
                            )
                        }
                    },
                    backgroundColor = primaryColor.copy(alpha = 0.95f),
                    contentColor = Color.White,
                    elevation = 0.dp
                )
            },
            bottomBar = {
                Surface(
                    elevation = 8.dp,
                    color = surfaceColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tổng",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${formatPrice(product.price * quantity)}đ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }

                        Button(
                            onClick = {
                                val addcart = Cart(context)
                                if(addcart.Add_cart(SessionManager.getInstance(context).getUserId(),product.product_id,product.category_id, quantity)){
                                    Toast.makeText(context,"Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()

                                }

                            },
                            modifier = Modifier
                                .height(48.dp)
                                .width(200.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thêm vào giỏ",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            backgroundColor = backgroundColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hình ảnh sản phẩm
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = product.product_name,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không có ảnh",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )

                    // Nút chế độ xem ảnh
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { /* Mở chế độ xem ảnh đầy đủ */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Xem ảnh đầy đủ",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Thông tin sản phẩm
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    backgroundColor = surfaceColor,
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Tên và giá
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.product_name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "${formatPrice(product.price)}đ",
                                fontSize = 22.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Đánh giá
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", ratingScore),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${ratings} đánh giá)",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Mô tả
                        Text(
                            text = "Mô tả",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Món ăn ngon với hương vị đặc trưng, được chế biến từ nguyên liệu tươi ngon, đảm bảo vệ sinh an toàn thực phẩm.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Chọn số lượng
                        Text(
                            text = "Số lượng",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (quantity > 1) quantity--
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Giảm",
                                    tint = if (quantity > 1) primaryColor else Color.Gray
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .width(24.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = { quantity++ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tăng",
                                    tint = primaryColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Thành phần
                        Text(
                            text = "Thành phần",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            // Các thẻ thành phần mẫu
                            val ingredients = listOf("Thịt", "Rau", "Gia vị", "Nước sốt")
                            ingredients.forEach { ingredient ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.LightGray.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
                    }
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: Dp = 0.dp,
    crossAxisSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val sequences = mutableListOf<List<Pair<Int, Int>>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Pair<Int, Int>>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Measure and cache all placeable
        val placeables = measurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }

        // Create sequences based on placeable
        placeables.forEachIndexed { index, placeable ->
            val mainAxisSize = placeable.width
            val crossAxisSize = placeable.height

            if (currentSequence.isEmpty()) {
                // Add item to current sequence
                currentSequence.add(Pair(index, crossAxisSize))
                currentMainAxisSize = mainAxisSize
                currentCrossAxisSize = crossAxisSize
            } else if (currentMainAxisSize + mainAxisSpacing.roundToPx() + mainAxisSize <= constraints.maxWidth) {
                // Add item + spacing to current sequence
                currentSequence.add(Pair(index, crossAxisSize))
                currentMainAxisSize += mainAxisSpacing.roundToPx() + mainAxisSize
                currentCrossAxisSize = maxOf(currentCrossAxisSize, crossAxisSize)
            } else {
                // Create a new sequence and cache current sequence info
                sequences.add(currentSequence.toList())
                crossAxisSizes.add(currentCrossAxisSize)
                currentSequence.clear()

                // Add item to new sequence
                currentSequence.add(Pair(index, crossAxisSize))
                currentMainAxisSize = mainAxisSize
                currentCrossAxisSize = crossAxisSize
            }
        }

        // Add last sequence and its size
        if (currentSequence.isNotEmpty()) {
            sequences.add(currentSequence.toList())
            crossAxisSizes.add(currentCrossAxisSize)
        }

        // Calculate cross axis position for each sequence
        var totalCrossAxisSize = 0
        for (i in crossAxisSizes.indices) {
            crossAxisPositions.add(totalCrossAxisSize)
            totalCrossAxisSize += crossAxisSizes[i] + if (i < crossAxisSizes.size - 1) crossAxisSpacing.roundToPx() else 0
        }

        // Set size of the parent layout
        val mainAxisSize = if (sequences.isNotEmpty()) {
            constraints.maxWidth
        } else {
            0
        }
        val crossAxisSize = if (sequences.isNotEmpty()) {
            crossAxisPositions.last() + crossAxisSizes.last()
        } else {
            0
        }

        layout(mainAxisSize, crossAxisSize) {
            // Place all children
            sequences.forEachIndexed { i, sequence ->
                var currentMainPosition = 0
                val childrenMainAxisSizes = sequence.map { placeables[it.first].width }
                val availableSpace = mainAxisSize - childrenMainAxisSizes.sum() - (sequence.size - 1) * mainAxisSpacing.roundToPx()
                val spacing = if (sequence.size > 1) {
                    mainAxisSpacing.roundToPx()
                } else {
                    0
                }

                sequence.forEachIndexed { j, (placeableIndex, _) ->
                    val placeable = placeables[placeableIndex]
                    placeable.place(
                        x = currentMainPosition,
                        y = crossAxisPositions[i]
                    )
                    currentMainPosition += placeable.width + spacing
                }
            }
        }
    }
}

@Composable
fun BorderedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    borderColor: Color = Color.White,
    fontSize: Int = 14
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(borderColor.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

// Import missing
