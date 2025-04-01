package com.example.thuchanh1

import android.os.Bundle
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.input.key.Key.Companion.Calendar


// Tạo data class để lưu thông tin người dùng
data class UserProfile(
   // val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val birthDate: String = "" // Thêm ngày sinh

)

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        oneTapClient = Identity.getSignInClient(this)
        analytics = Firebase.analytics

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("47573765231-9qnqrgusqpjv52ffshkaaeo0bofgus2n.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        var loginStatus by mutableStateOf<String?>(null)
        var userProfile by mutableStateOf<UserProfile?>(null)

        val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken) { success, user ->
                            if (success && user != null) {
                                analytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                                    putString(FirebaseAnalytics.Param.METHOD, "google")
                                    putString("email", user.email)
                                })
                                userProfile = user
                            } else {
                                loginStatus = "Google Sign-In Failed\nAuthentication error!"
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error: ${e.message}")
                    loginStatus = "Google Sign-In Failed\nUser canceled the Google sign-in process."
                }
            } else {
                loginStatus = "Google Sign-In Failed\nUser canceled the Google sign-in process."
            }
        }

        setContent {
            AppNavigation(
                loginStatus = loginStatus,
                userProfile = userProfile,
                onLoginClick = {
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            launcher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                        }
                        .addOnFailureListener { e ->
                            Log.e("GoogleSignIn", "Error starting sign-in: ${e.message}")
                            loginStatus = "Google Sign-In Failed\n${e.message}"
                        }
                },
                onLogout = {
                    auth.signOut()
                    userProfile = null
                }
            )
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, UserProfile?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userProfile = UserProfile(
                            //uid = user.uid,
                            name = user.displayName ?: "",
                            email = user.email ?: "",
                            photoUrl = user.photoUrl?.toString(),

                        )
                        onResult(true, userProfile)
                    } else {
                        onResult(false, null)
                    }
                } else {
                    Log.e("FirebaseAuth", "Error signing in: ${task.exception?.message}")
                    onResult(false, null)
                }
            }
    }
}

@Composable
fun AppNavigation(
    loginStatus: String?,
    userProfile: UserProfile?,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // Observe userProfile and navigate when it changes to a non-null value
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            navController.navigate("home") {
                // Clear the back stack so user can't go back to login
                popUpTo("login") { inclusive = true }
            }
        } else {
            // Navigate back to login if userProfile becomes null (logout)
            if (navController.currentDestination?.route != "login") {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                loginStatus = loginStatus,
                onLoginClick = onLoginClick
            )
        }
        composable("home") {
            userProfile?.let { profile ->
                HomeScreen(
                    userProfile = profile,
                    onBackClick = { onLogout() }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(loginStatus: String?, onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.background_logo),
                        contentDescription = "Background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        painter = painterResource(id = R.drawable.uth_logo),
                        contentDescription = "UTH Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(text = "SmartTasks", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "A simple and efficient to-do app", fontSize = 14.sp, color = Color(0xFF3991D8))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Ready to explore? Log in to get started.", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0E7FF)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIGN IN WITH GOOGLE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001F3F)
                    )
                }

                // Display login status if available
                loginStatus?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it, fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userProfile: UserProfile, onBackClick: () -> Unit) {
    // State để theo dõi ngày sinh của người dùng
    var selectedDate by remember { mutableStateOf(userProfile.birthDate) }
    // State để kiểm soát hiển thị date picker dialog
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3991D8))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút quay về (bên trái)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Profile",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Spacer để cân đối với nút bên trái
            Spacer(modifier = Modifier.size(40.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Hiển thị ảnh đại diện nếu có
            userProfile.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.name.firstOrNull()?.toString() ?: "?",
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Card hiển thị thông tin chi tiết
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin tài khoản",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(title = "Email:", value = userProfile.email)
                    ProfileInfoRow(title = "Name:", value = userProfile.name)

                    // Thêm dòng chọn ngày sinh
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Date of Birth:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .wrapContentWidth()
                        )

                        Text(
                            text = if (selectedDate.isNotEmpty()) selectedDate else " ",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )

                        Button(
                            onClick = { showDatePicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3991D8))
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // DatePicker Dialog sử dụng Jetpack Compose
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        // Chuyển đổi từ milliseconds thành ngày tháng năm
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = java.util.Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            val month = calendar.get(java.util.Calendar.MONTH) + 1 // Month is 0-indexed
                            val year = calendar.get(java.util.Calendar.YEAR)
                            selectedDate = "$day/$month/$year"
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(
                state = datePickerState,
                // Giới hạn ngày tối đa là hiện tại
            )
        }
    }
}
@Composable
fun ProfileInfoRow(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
                .align(Alignment.Start)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}

