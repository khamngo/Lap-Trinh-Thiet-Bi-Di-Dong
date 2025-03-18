package com.example.tuan3_bai2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tuan3_bai2.ui.theme.Tuan3_Bai2Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Tuan3_Bai2Theme {
                AppNavigator()
            }
        }
    }
}
@Composable
fun AppNavigator() {
    val navigator = rememberNavController()
    NavHost(navController = navigator, startDestination = "introScreen") {
        composable("introScreen") { IntroScreen(navigator) }
        composable("pageOne") { PageOne(navigator) }
        composable("pageTwo") { PageTwo(navigator) }
        composable("pageThree") { PageThree(navigator) }
    }
}

@Composable
fun IntroScreen(navigator: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navigator.navigate("pageOne") {
            popUpTo("introScreen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(102.dp, 70.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "UTH TaskMaster",
                fontSize = 25.sp,
                color = Color(0xFF006EE9)
            )
        }
    }
}

@Composable
fun PageOne(navigator: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(painterResource(R.drawable.blue), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
            }
            Text("Skip", textAlign = TextAlign.End)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painterResource(R.drawable.pic1), contentDescription = null, Modifier.size(350.dp, 261.dp))
            Spacer(Modifier.height(20.dp))
            Text("Effortless Time Management", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Text(
                "Manage tasks efficiently with priority-based scheduling and smart tracking.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxSize(),
        ) {
            Button(
                onClick = { navigator.navigate("pageTwo") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(53.dp)
            ) {
                Text("Next", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun PageTwo(navigator: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.blue), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
            }
            Text("Skip", textAlign = TextAlign.End)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painterResource(R.drawable.pic2), contentDescription = null, Modifier.size(350.dp, 261.dp))
            Spacer(Modifier.height(20.dp))
            Text("Boost Productivity", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Text(
                "Stay on top of tasks with prioritized schedules and performance tracking.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxHeight()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { navigator.navigate("pageOne") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(53.dp, 53.dp)
                ) {
                    Image(painterResource(R.drawable.arrowback), contentDescription = null, modifier = Modifier.size(40.dp))
                }
                Button(
                    onClick = { navigator.navigate("pageThree") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3), contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(53.dp).padding(start = 20.dp)
                ) {
                    Text("Next", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun PageThree(navigator: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.white), contentDescription = null, modifier = Modifier.size(10.dp))
                Spacer(Modifier.width(3.dp))
                Image(painterResource(R.drawable.blue), contentDescription = null, modifier = Modifier.size(10.dp))
            }
            Text("Skip", textAlign = TextAlign.End)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painterResource(R.drawable.pic3), contentDescription = null, Modifier.size(350.dp, 261.dp))
            Spacer(Modifier.height(20.dp))
            Text("Smart Reminders", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Text(
                "Never miss a task with timely notifications and alerts.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Button(
                onClick = { navigator.navigate("pageTwo") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3)),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(53.dp, 53.dp)
            ) {
                Image(painterResource(R.drawable.arrowback), contentDescription = null, modifier = Modifier.size(40.dp))
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(53.dp).padding(start = 20.dp)
            ) {
                Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Tuan3_Bai2Theme {
       PageThree(navigator = rememberNavController()) }
}