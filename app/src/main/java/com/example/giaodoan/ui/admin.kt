package com.example.giaodoan.ui


import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import com.example.giaodoan.model.Category
import com.example.giaodoan.model.Product
import java.io.File
import coil.compose.AsyncImage
import com.example.giaodoan.R
import androidx.navigation.NavController

data class ProductDisplay(
    val product_id: Int,
    val category_id: Int,
    val category_name: String, // Thêm trường này để lưu tên danh mục
    val product_name: String,
    val price: Long,
    val img: String,
)
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val active: Boolean,
)
data class CategoryDisplay(val id: Int, val name: String, val description: String)

@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
    val categoryModel = Category(context)
    val isSuccessful = categoryModel.Select_category()
    val categories = isSuccessful.map {
        CategoryDisplay(
            id = it.category_id,
            name = it.category_name.toString(),
            description = it.getDescription().toString()
        )
    }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Thêm sản phẩm", "Thêm loại sản phẩm", "Quản lý người dùng")

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
                text = "Quản lý hệ thống",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Row{
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable{
                            navController.navigate("danhSachMonAn")
                        },
                    painter = painterResource(R.drawable.home),
                    contentDescription = "home",
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable{
                            navController.navigate("profile")
                        },
                    painter = painterResource(R.drawable.human),
                    contentDescription = "human",
                )
            }
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
            0 -> AddProductTab(categories)
            1 -> AddCategoryTab(categories)
            2 -> UserManagementTab()
        }
    }
}

@Composable
fun AddProductTab(categories: List<CategoryDisplay>) {
    val context = LocalContext.current
    val productModel = Product(context)

    val categoryMap = remember(categories) {
        categories.associateBy({ it.id }, { it.name })
    }
    val productList = productModel.Select_product().map {
        ProductDisplay(
            product_id = it.product_id,
            category_id = it.category_id,
            category_name = it.category_name,
            product_name = it.product_name,
            price = it.price,
            img = it.img
        )
    }
    var products by remember { mutableStateOf(productList) }
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(0) } // Đã sửa: selectedCategory -> selectedCategoryId (Int)
    var showDropdown by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedProduct by remember { mutableStateOf<ProductDisplay?>(null) }

    var currentProductTab by remember { mutableStateOf(0) }
    val productTabs = listOf("Thêm sản phẩm", "Danh sách sản phẩm")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedImageUri = saveImageToInternalStorage(context, it)
            imageUri = savedImageUri ?: it
            Log.d("ImageUpload", "Saved image URI: $imageUri")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
    ) {
        // Tab cho sản phẩm
        TabRow(
            selectedTabIndex = currentProductTab,
            backgroundColor = Color.White,
            contentColor = Color(0xFF1976D2)
        ) {
            productTabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = currentProductTab == index,
                    onClick = { currentProductTab = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (currentProductTab) {
            0 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Thêm sản phẩm mới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Tên sản phẩm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = { productPrice = it },
                        label = { Text("Giá sản phẩm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Chọn danh mục"

                        OutlinedTextField(
                            value = selectedCategoryName, // Hiển thị tên danh mục
                            onValueChange = {},
                            label = { Text("Danh mục") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { showDropdown = !showDropdown }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(onClick = {
                                    selectedCategoryId = category.id
                                    showDropdown = false
                                }) {
                                    Text(text = category.name)
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE0E0E0))
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.upload),
                            contentDescription = "Upload",
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tải lên hình ảnh", color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thêm sản phẩm", color = Color.White)
                    }
                }
            }
            1 -> {
                ProductListTab(
                    products = products,
                    onEditClick = { product ->
                        selectedProduct = product
                        productName = product.product_name
                        productPrice = product.price.toString()
                        selectedCategoryId = product.category_id // Đặt ID danh mục khi chỉnh sửa
                        showEditDialog = true
                    },
                    onDeleteClick = { product ->
                        val result = productModel.Delete_product(product.product_id)
                        if (result) {
                            products = products.filter { it.product_id != product.product_id }
                            Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có chắc muốn thêm sản phẩm này?") },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val price = productPrice.toLongOrNull() ?: 0L
                            var imageResId =""
                            if (imageUri != null) {
                                imageResId = imageUri.toString()
                            }


                            val isSuccess = productModel.Add_product(
                                selectedCategoryId,
                                productName,
                                price,
                                imageResId
                            )

                            if (isSuccess) {
                                val categoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Không có danh mục"

                                val newProduct = ProductDisplay(
                                    product_id = -1,
                                    category_id = selectedCategoryId,
                                    category_name = categoryName,
                                    product_name = productName,
                                    price = price,
                                    img = imageResId
                                )

                                products = products + newProduct

                                productName = ""
                                productPrice = ""
                                selectedCategoryId = 0
                                imageUri = null

                                products = productModel.Select_product().map {
                                    ProductDisplay(
                                        product_id = it.product_id,
                                        category_id = it.category_id,
                                        category_name = categoryMap[it.category_id] ?: "Không có danh mục",
                                        product_name = it.product_name,
                                        price = it.price ?: 0L,
                                        img = it.img
                                    )
                                }

                                Toast.makeText(context, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Sản phẩm đã tồn tại", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.d("Lỗi err:", e.message.toString())
                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                        showConfirmDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        ProductEditDialog(
            product = selectedProduct!!,
            categories = categories,
            onDismissRequest = { showEditDialog = false },
            onConfirm = { updatedProduct ->
                val isSuccess = productModel.Update_product(
                    updatedProduct.product_id,
                    updatedProduct.category_id, // Sử dụng category_id
                    updatedProduct.product_name,
                    updatedProduct.price,
                    updatedProduct.img
                )

                if (isSuccess) {
                    products = products.map {
                        if (it.product_id == updatedProduct.product_id) updatedProduct else it
                    }

                    Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Không thể cập nhật sản phẩm", Toast.LENGTH_SHORT).show()
                }

                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProductListTab(
    products: List<ProductDisplay>,
    onEditClick: (ProductDisplay) -> Unit,
    onDeleteClick: (ProductDisplay) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Danh sách sản phẩm",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có sản phẩm nào",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onEditClick = { onEditClick(product) },
                        onDeleteClick = { onDeleteClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: ProductDisplay,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hiển thị ảnh
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = product.product_name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = product.product_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                val formattedPrice = remember(product.price) {
                    String.format("%,d", product.price).replace(",", ".")
                }

                Text(
                    text = "$formattedPrice đ",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Danh mục: ${product.category_name}", // Hiển thị category_name thay vì category_id
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Các nút chức năng
            Column {
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2)),
                    modifier = Modifier
                        .width(80.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text("Sửa", color = Color.White)
                }

                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                    modifier = Modifier.width(80.dp)
                ) {
                    Text("Xóa", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProductEditDialog(
    product: ProductDisplay,
    categories: List<CategoryDisplay>, // Đã sửa: Nhận danh sách CategoryDisplay thay vì String
    onDismissRequest: () -> Unit,
    onConfirm: (ProductDisplay) -> Unit,
) {
    var name by remember { mutableStateOf(product.product_name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var categoryId by remember { mutableStateOf(product.category_id) } // Thay đổi từ String -> Int
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Chỉnh sửa sản phẩm") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown danh mục - đã sửa để hiển thị tên nhưng lưu ID
                Box(modifier = Modifier.fillMaxWidth()) {
                    val selectedCategoryName = categories.find { it.id == categoryId }?.name ?: "Chọn danh mục"

                    OutlinedTextField(
                        value = selectedCategoryName, // Hiển thị tên danh mục
                        onValueChange = {},
                        label = { Text("Danh mục") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { showCategoryDropdown = !showCategoryDropdown }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                categoryId = category.id // Lưu ID của danh mục
                                showCategoryDropdown = false
                            }) {
                                Text(text = category.name) // Hiển thị tên danh mục
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toLongOrNull() ?: 0L
                    // Lấy tên danh mục dựa trên ID
                    val categoryName = categories.find { it.id == categoryId }?.name ?: "Không có danh mục"

                    val updatedProduct = product.copy(
                        product_name = name,
                        price = priceValue,
                        category_id = categoryId, // Lưu ID
                        category_name = categoryName // Cập nhật tên danh mục
                    )
                    onConfirm(updatedProduct)
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Hủy")
            }
        }
    )
}


@Composable
fun AddCategoryTab(categories: List<CategoryDisplay>) {
    var categoryList by remember { mutableStateOf(categories) }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thêm loại sản phẩm mới",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Tên loại sản phẩm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoryDescription,
                onValueChange = { categoryDescription = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm loại sản phẩm", color = Color.White)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Danh sách loại sản phẩm hiện có",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryList) { category ->
                    CategoryItem(
                        name = category.name,
                        description = category.description,
                        onDeleteClick = {
                            val categoryModel = Category(context)
                            if (categoryModel.Delete_category(category.id)) {
                                categoryList = categoryList.filter { it.id != category.id }
                                Toast.makeText(context, "Đã xóa loại sản phẩm", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Không thể xóa loại sản phẩm này", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có chắc muốn thêm loại sản phẩm này?") },
            confirmButton = {
                Button(
                    onClick = {
                        val categoryModel = Category(context)
                        if (categoryModel.Add_category(categoryName, categoryDescription)) {
                            val updatedCategories = categoryModel.Select_category().map {
                                CategoryDisplay(
                                    id = it.category_id,
                                    name = it.category_name.toString(),
                                    description = it.getDescription().toString()
                                )
                            }
                            categoryList = updatedCategories

                            categoryName = ""
                            categoryDescription = ""

                            Toast.makeText(context, "Thêm thành công", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Tên loại sản phẩm này đã có", Toast.LENGTH_LONG).show()
                        }
                        showConfirmDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}
@Composable
fun CategoryItem(
    name: String,
    description: String,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thông tin danh mục
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                modifier = Modifier.width(80.dp)
            ) {
                Text("Xóa", color = Color.White)
            }
        }
    }
}

@Composable
fun UserManagementTab() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Tất cả") }
    var showRoleDropdown by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val userList = remember {
        listOf(
            User("1", "admin", "admin@example.com", "Admin", true),
            User("2", "staffuser", "staff1@example.com", "Staff", true),
            User("3", "customerA", "customer1@example.com", "User", true),
            User("4", "customerB", "customer2@example.com", "User", false),
            User("5", "staffuser2", "staff2@example.com", "Staff", true)
        )
    }

    val filteredUsers = userList.filter { user ->
        (searchQuery.isEmpty() ||
                user.username.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)) &&
                (selectedRole == "Tất cả" || user.role == selectedRole)
    }

    val roles = listOf("Tất cả", "Admin", "Staff", "User")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = " Quảnlý người dùng",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tìm kiếm theo tên/email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lọc theo vai trò:",
                modifier = Modifier.padding(end = 8.dp)
            )

            Box {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.width(150.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.clickable { showRoleDropdown = !showRoleDropdown }
                        )
                    }
                )

                DropdownMenu(
                    expanded = showRoleDropdown,
                    onDismissRequest = { showRoleDropdown = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(onClick = {
                            selectedRole = role
                            showRoleDropdown = false
                        }) {
                            Text(text = role)
                        }
                    }
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy người dùng nào",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    UserItem(
                        user = user,
                        onEditClick = {
                            selectedUser = user
                            showEditDialog = true
                        },
                        onToggleActiveClick = {
                            selectedUser = user
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }

        Text(
            text = "Tổng số: ${filteredUsers.size} người dùng",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
    if (showEditDialog && selectedUser != null) {
        UserEditDialog(
            user = selectedUser!!,
            onDismissRequest = { showEditDialog = false },
            onConfirm = { editedUser ->
                showEditDialog = false
            }
        )
    }

    if (showConfirmDialog && selectedUser != null) {
        val actionText = if (selectedUser!!.active) "vô hiệu hóa" else "kích hoạt"

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có chắc muốn $actionText tài khoản ${selectedUser!!.username}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun UserItem(
    user: User,
    onEditClick: () -> Unit,
    onToggleActiveClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp,
        backgroundColor = if (user.active) Color.White else Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val roleColor = when (user.role) {
                        "Admin" -> Color(0xFFD32F2F)
                        "Staff" -> Color(0xFF1976D2)
                        else -> Color(0xFF388E3C)
                    }

                    Card(
                        backgroundColor = roleColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = user.role,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val statusText = if (user.active) "Đang hoạt động" else "Đã vô hiệu hóa"
                    val statusColor = if (user.active) Color(0xFF388E3C) else Color.Gray

                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }

            Column {
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2)),
                    modifier = Modifier
                        .width(120.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text("Chỉnh sửa", color = Color.White)
                }

                val buttonText = if (user.active) "Vô hiệu hóa" else "Kích hoạt"
                val buttonColor = if (user.active) Color(0xFFD32F2F) else Color(0xFF388E3C)

                Button(
                    onClick = onToggleActiveClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(buttonText, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun UserEditDialog(
    user: User,
    onDismissRequest: () -> Unit,
    onConfirm: (User) -> Unit,
) {
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var role by remember { mutableStateOf(user.role) }
    var showRoleDropdown by remember { mutableStateOf(false) }

    val roles = listOf("Admin", "Staff", "User")

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Chỉnh sửa người dùng") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên người dùng") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        label = { Text("Vai trò") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { showRoleDropdown = !showRoleDropdown }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showRoleDropdown,
                        onDismissRequest = { showRoleDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        roles.forEach { roleOption ->
                            DropdownMenuItem(onClick = {
                                role = roleOption
                                showRoleDropdown = false
                            }) {
                                Text(text = roleOption)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val editedUser = user.copy(
                        username = username,
                        email = email,
                        role = role
                    )
                    onConfirm(editedUser)
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Hủy")
            }
        }
    )
}
fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "product_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        Uri.fromFile(file)
    } catch (e: Exception) {
        Log.e("SaveImage", "Error saving image: ${e.message}")
        null
    }
}