package com.example.thuchanh2


import android.annotation.SuppressLint
import android.graphics.Color.rgb
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument


// Data Model
data class Task(
    val title: String,
    val description: String,
    val subtasks: List<Subtask>,
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskApp()
        }
    }
}

@Composable
fun TaskApp() {
    val navController = rememberNavController()
    var allTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            allTasks = fetchTasks()
        }
    }

    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TaskListScreen(navController, allTasks)
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(top = 21.dp)
        )
        {
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
                            painter = painterResource(R.drawable.logo),
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
                    items(displayedTasks) { task ->
                        TaskItem(task, navController)
                    }
                }
            }
        }
    }
}


@SuppressLint("UseKtx")
@Composable
fun TaskItem(task: Task, navController: NavController) {
    val backgroundColor = when (task.priority) {
        "High" -> Color(228, 174, 181,255)
        "Medium" -> Color(165, 181, 27,255)
        "Low" -> Color(158, 214, 239,255)
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
            Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = task.description, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.Black, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                navController.navigate("detail/${task.title}/${task.description}")
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


@Composable

fun DetailTaskScreen(task: Task, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút Back + Tiêu đề
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
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
                color = Color.Blue,
                modifier = Modifier.padding(start = 8.dp)
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

                    Text(
                        text = subtask.title,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
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
