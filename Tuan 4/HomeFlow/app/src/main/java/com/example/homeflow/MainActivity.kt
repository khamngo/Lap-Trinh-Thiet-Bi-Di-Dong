package com.example.homeflow
import android.R.attr.checked
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.homeflow.ui.theme.HomeFlowTheme
import kotlinx.coroutines.*

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeFlowTheme {
                TaskApp()
            }
        }
    }
}
@Composable
fun TaskApp() {
    val navController = rememberNavController()
    var allTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy dữ liệu từ API khi app chạy
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            allTasks = fetchTasks()
        }
    }

    NavHost(navController = navController, startDestination = "introScreen") {
        // Màn hình giới thiệu
        composable("introScreen") { IntroScreen(navController) }
        composable("pageOne") { PageOne(navController) }
        composable("pageTwo") { PageTwo(navController) }
        composable("pageThree") { PageThree(navController) }  // Trang cuối của intro

        // Màn hình danh sách công việc
        composable("taskList") {
            TaskListScreen(navController, allTasks)
        }

        // Màn hình chi tiết công việc
        composable(
            "detail/{title}/{description}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val task = allTasks.find { it.title == title && it.description == description }

            task?.let {
                DetailTaskScreen(task = it, onBack = { navController.popBackStack() })
            }
        }
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
                onClick = { navigator.navigate("taskList") },  // Chuyển đến TaskListScreen
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A96F3), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(53.dp).padding(start = 20.dp)
            ) {
                Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}




// Data Model

data class Task(
    val title: String,
    val description: String,
    val subtasks: List<Subtask>,
    val category: String,
    val attachments: List<Attachment>,
    val priority: String,
    val id: Int,
    val status: String,
    val dueDate: String,
    val reminders: List<Reminder>
)

data class Subtask(   val id: Int, val title: String, val isCompleted: Boolean)
data class Attachment(   val id: Int,val fileName: String, val fileUrl: String)
data class Reminder(   val id: Int,val time: String, val type: String)



@Composable
fun TaskListScreen(navController: NavController, allTasks1: List<Task>) {
    var allTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var displayedTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            allTasks = fetchTasks()
            displayedTasks = allTasks
        }
    }

    // 📌 Bọc toàn bộ trong Box để căn chỉnh
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Chừa khoảng trống cho Navigation bar
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(top = 21.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.background_underlogonoti),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp, 100.dp)
                        .align(Alignment.TopEnd)
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Logo
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(33, 150, 243).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo2),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp, 35.dp)
                            )
                        }

                        Column {
                            Text(
                                "SmartTasks",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(33, 150, 243)
                            )
                            Text(
                                "A simple to-do app",
                                fontSize = 8.sp,
                                color = Color(33, 150, 243)
                            )
                        }

                        Image(
                            painter = painterResource(R.drawable.icon_noti),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                        if (displayedTasks.isNotEmpty()) {
                            items(displayedTasks) { task ->
                                TaskItem(task, navController)
                            }
                        } else {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 50.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.notaskyet),
                                        contentDescription = "No tasks available",
                                        modifier = Modifier.size(300.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 📌 Navigation Bar và nút Add (đưa xuống đáy)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(20.dp), // Khoảng cách từ mép màn hình
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_home),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color(0xFF2196F3)
                    )
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color(0xFF333333B2)
                    )
                    Spacer(modifier = Modifier.size(50.dp))
                    Icon(
                        painterResource(R.drawable.baseline_library),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color(0xFF333333B2)
                    )
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color(0xFF333333B2)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-27).dp)
                    .background(Color(0xFF2196F3), shape = RoundedCornerShape(40.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
        }
    }
}


@SuppressLint("UseKtx")
@Composable
fun TaskItem(task: Task,navController: NavController) {
    val backgroundColor = when (task.priority) {
        "High" -> Color(228, 174, 181, 255)
        "Medium" -> Color(165, 181, 27, 255)
        "Low" -> Color(158, 214, 239, 255)
        else -> Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(backgroundColor, shape = RoundedCornerShape(15.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var isChecked by remember { mutableStateOf(false) }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Black,
                        checkmarkColor = Color.White,
                        uncheckedColor = Color.Gray
                    )
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("detail/${task.title}/${task.description}")
                        })
                    Text(text = task.description, fontSize = 14.sp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Status: ${task.status}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = task.dueDate, fontSize = 12.sp)
            }
        }
    }
}

@Composable


fun DetailTaskScreen(task: Task, onBack: () -> Unit) {
    val backgroundColor = when (task.priority) {
        "High" -> Color(228, 174, 181, 255)
        "Medium" -> Color(165, 181, 27, 255)
        "Low" -> Color(158, 214, 239, 255)
        else -> Color.White
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút Back + Tiêu đề
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.weight(1f))
            {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.Blue
                )
            }
            Text(
                text = "Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(33, 150, 243),
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )
            Icon(
                painter = painterResource(R.drawable.trash),
                contentDescription = "Trash",
                modifier = Modifier.weight(1f),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tiêu đề & Mô tả
        Text(
            text = task.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = task.description,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Thông tin Task (Category, Status, Priority)
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape = RoundedCornerShape(15.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.detail_category),
                    contentDescription = "Category",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column() {
                    Text(text = "Category", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = task.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.detail_status),
                    contentDescription = "Status",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column() {
                    Text(text = "Status", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = task.status, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.detail_priority),
                    contentDescription = "Priority",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(){
                    Text(text = "Priority", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = task.priority, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Danh sách Subtasks
        if (task.subtasks.isNotEmpty()) {
            Text(
                text = "Subtasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            task.subtasks.forEach { subtask ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var isChecked by remember { mutableStateOf(false) }
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Black,
                                checkmarkColor = Color.White,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = subtask.title,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Danh sách Attachments
        if (task.attachments.isNotEmpty()) {
            Text(
                text = "Attachments",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            task.attachments.forEach { attachment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                        Text(
                            text = attachment.fileName,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                }
            }
        }
    }
}


// Fetch dữ liệu từ API
suspend fun fetchTasks(): List<Task> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://amock.io/api/researchUTH/tasks")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONObject(response).getJSONArray("data")

                (0 until jsonArray.length()).map { index ->
                    val jsonTask = jsonArray.getJSONObject(index)
                    Task(
                        id = jsonTask.getInt("id"),
                        title = jsonTask.getString("title"),
                        description = jsonTask.getString("description"),
                        priority = jsonTask.getString("priority"),
                        category = jsonTask.getString("category"),
                        status = jsonTask.getString("status"),
                        dueDate = jsonTask.getString("dueDate"),
                        subtasks = jsonTask.getJSONArray("subtasks")?.let { array ->
                            (0 until array.length()).map { i ->
                                val subtask = array.getJSONObject(i)
                                Subtask(
                                    id = subtask.getInt("id"),
                                    title = subtask.getString("title"),
                                    isCompleted = subtask.getBoolean("isCompleted")
                                )
                            }
                        } ?: emptyList(),
                        attachments = jsonTask.getJSONArray("attachments")?.let { array ->
                            (0 until array.length()).map { i ->
                                val attachment = array.getJSONObject(i)
                                Attachment(
                                    id = attachment.getInt("id"),
                                    fileName = attachment.getString("fileName"),
                                    fileUrl = attachment.getString("fileUrl")
                                )
                            }
                        } ?: emptyList(),
                        reminders = jsonTask.getJSONArray("reminders")?.let { array ->
                            (0 until array.length()).map { i ->
                                val reminder = array.getJSONObject(i)
                                Reminder(
                                    id = reminder.getInt("id"),
                                    time = reminder.getString("time"),
                                    type = reminder.getString("type")
                                )
                            }
                        } ?: emptyList()
                    )
                }
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

