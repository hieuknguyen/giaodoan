package com.example.giaodoan.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import com.example.giaodoan.database.api.ApiService
import com.example.giaodoan.model.User
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Apiemail(
    val success: Boolean,
    val message: String,
    val error: String
)

class sen_code() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.elasticemail.com/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)
    suspend fun getGroupApi(email: String,otpCode: String) {
        apiService.sendEmail(
            apiKey = "089C4BE25B99FA3F72A36648EE30C343E39417E5A9A7558887373172EC5FB7251E6325AFAFBC6E8CFE2C5805E7923DD2",
            from = "a@quackquack.io.vn",
            fromName = "YourAppName",
            to = email,
            subject = "Mã xác thực OTP của bạn",
            bodyText = "Mã OTP của bạn là: $otpCode",
            bodyHtml = "<h2>Mã OTP của bạn là:</h2><p style='font-size:24px;color:blue;'>$otpCode</p>"
        )

    }
}
class register() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedViewModel: SharedViewModel by viewModels()
            val navController = rememberNavController()

            register(
                navController = navController,
                sharedViewModel = sharedViewModel,)
        }
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun register(sharedViewModel: SharedViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showQrDialog by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    text = "Đăng ký",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên người dùng") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                // Nhập Mật khẩu
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

                // Nhập lại Mật khẩu
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )


                Button(
                    onClick = {
                        if(username.isEmpty() && password.isEmpty()){
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show()
                        }
                        else if(username.isEmpty()){
                            Toast.makeText(context, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_LONG).show()
                        }
                        else if(email.isEmpty()){
                            Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_LONG).show()
                        }
                        else if(password.isEmpty()){
                            Toast.makeText(context, "Vui lòng nhập mật khẩu", Toast.LENGTH_LONG).show()
                        }
                        else if (password == confirmPassword && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                            scope.launch {
                                otpCode = (100000..999999).random().toString()
                                sen_code().getGroupApi(email, otpCode)
                                Toast.makeText(context,otpCode.toString(), Toast.LENGTH_SHORT).show()
                                showQrDialog = true

                            }
                        }
                        else{

                            Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_LONG).show()
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
                ) {
                    Text(
                        text = "Đăng ký",
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = "Đã có tài khoản? Đăng nhập ngay",
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                )
                if (showQrDialog) {
                    Dialog(onDismissRequest = { showQrDialog = false }) {
                        androidx.compose.material3.Card(
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
                                    text = "Nhập mã xác thực email",
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
                                    OutlinedTextField(
                                        value = otp,
                                        onValueChange = { otp = it },
                                        label = { Text("Nhập mã otp") },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextStyle(fontSize = 16.sp),
                                        visualTransformation = PasswordVisualTransformation(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Black,
                                            unfocusedBorderColor = Color.Gray
                                        )
                                    )

                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        if(otp == otpCode){
                                            val loginModel = User(context)
                                            if(loginModel.register(username,email,password,"User")){
                                                Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                                showQrDialog = false
                                                navController.navigate("login") {
                                                    popUpTo("taskList") { inclusive = true }
                                                }
                                            }else{
                                                Toast.makeText(context, "Đã cố tên đăng nhập này rồi", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        else{
                                            Toast.makeText(context, "mã otp không khớp", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Xong")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}