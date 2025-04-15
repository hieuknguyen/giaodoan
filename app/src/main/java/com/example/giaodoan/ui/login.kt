package com.example.giaodoan.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.giaodoan.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController
import com.example.giaodoan.database.DatabaseHelper
import com.example.giaodoan.model.SessionManager

class login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginModel = User(this)
            loginModel.register("Hieuknguye","hieu@gmail.com","123","Admin")
//            DatabaseHelper(this).deleteDatabase(this)
            TodoNavigation()
        }
    }
}
class SharedViewModel : ViewModel() {
    private val _sharedData = MutableStateFlow<String?>(null)
    val sharedData: StateFlow<String?> = _sharedData.asStateFlow()
    fun updateData(data: String) {
        _sharedData.value = data
    }
}
@Composable
fun TodoNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("Login") {
            if(SessionManager.getInstance(LocalContext.current).isLoggedIn() && SessionManager.getInstance(LocalContext.current).getRole() == "User"){
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }else if(SessionManager.getInstance(LocalContext.current).isLoggedIn() && SessionManager.getInstance(LocalContext.current).isAdmin){
                navController.navigate("admin") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            else{
            LoginScreen( navController)
            }
        }
        composable("register") {
            register(sharedViewModel,navController)
        }
        composable("cart") {

            GioHangScreenByCategory()
        }
        composable("profile") {
            UserProfileScreen(navController)
        }
        composable("danhSachMonAn") {
            DanhSachMonAn(navController)
        }
        composable("admin") {
            AdminScreen(navController)

        }
        composable("login"){
            if(SessionManager.getInstance(LocalContext.current).isLoggedIn() && SessionManager.getInstance(LocalContext.current).getRole() == "User"){
                navController.navigate("danhSachMonAn") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            else if(SessionManager.getInstance(LocalContext.current).isLoggedIn() && SessionManager.getInstance(LocalContext.current).isAdmin){
                navController.navigate("admin") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }else{
                LoginScreen(navController)
            }

            }
    }
}



@Composable
fun LoginScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card đăng nhập
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Đăng nhập",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("username") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Button(
                    onClick =
                        {
                            val loginModel = User(context)
                            val isLoginSuccessful = loginModel.checkLogin(username, password)
                            if(isLoginSuccessful != null){
                                val a = SessionManager.getInstance(context)
                                val currentContext = context
                                SessionManager.getInstance(currentContext).saveLoginInfo(
                                    isLoginSuccessful.user_id,
                                    isLoginSuccessful.username,
                                    isLoginSuccessful.password,
                                    isLoginSuccessful.email,
                                    isLoginSuccessful.phone,
                                    isLoginSuccessful.role
                                )
                                if(SessionManager.getInstance(context).isAdmin){
                                    navController.navigate("admin"){
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                                else if(a.getRole() == "User"){
                                    navController.navigate("danhSachMonAn"){
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }
                            else{
                                Toast.makeText(context,"Tên đăng nhập hoặc mật khẩu không đúng!",
                                    Toast.LENGTH_LONG).show()
                            }
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ){
                    Text(
                        text = "Đăng nhập",
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = "Chưa có tài khoản? Đăng ký ngay",
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            navController.navigate("register")
                        },
                )
            }
        }
    }
}



