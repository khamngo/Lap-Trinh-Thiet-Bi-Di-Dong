package com.example.thuchanh1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState

//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thuchanh1.ui.theme.thuchanh1theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            thuchanh1theme {
                Controller()
            }
        }
    }
}


@Composable
fun Controller() {
    val navConTroller = rememberNavController()
    NavHost(navController = navConTroller, startDestination = "home") {
        composable("home") { MainScreen(navConTroller) }
        composable ("2ndpage"){ SecondPage(navConTroller) }
        composable("3rdPage/{quote}") { backStackEntry ->
            val quote = backStackEntry.arguments?.getString("quote") ?: "No quote available"
            ThreePage(navConTroller, quote)
        }

    }
}



@Composable
fun MainScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.isStatusBarVisible = true // Hiển thị Status Bar
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(19.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.composelogo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 120.dp)
                    .size(216.dp, 233.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text("Navigation ", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(20.dp))

            Text(
                "is a framework that simplifies the implementation of navigation between different UI components (activities, fragments, or composables) in an app",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
            )
        }

        Button(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = { navController.navigate("2ndpage") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
            )
        ) {
            Text(
                "Push",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun SecondPage(navController: NavController) {
    val listState = rememberLazyListState()
    val quotes = listOf(
        "01 | The only way to do great work is to love what you do.",
        "02 | Success is not the key to happiness. Happiness is the key to success.",
        "03 | Don't watch the clock; do what it does. Keep going.",
        "04 | The future depends on what you do today."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(19.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "UI Components List",
            color = Color(0xFF2196F3),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            items(quotes) { quote -> // ✅ Fix lỗi: truyền danh sách vào items()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(228, 174, 181, 255), shape = RoundedCornerShape(15.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quote,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            navController.navigate("3rdPage/${Uri.encode(quote)}")
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Arrow",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ThreePage(navController: NavController, quote: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nút quay lại
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị câu trích dẫn nhận từ SecondPage
        Text(
            text = "“$quote”",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hình ảnh chứa câu trích dẫn
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(3f / 4f),
            shape = RoundedCornerShape(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.quote_image), // Thay bằng ảnh của bạn
                contentDescription = "Quote",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút BACK TO ROOT
        Button(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = { navController.navigate("home") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
            )
        ) {
            Text(
                "Back To Root",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}







