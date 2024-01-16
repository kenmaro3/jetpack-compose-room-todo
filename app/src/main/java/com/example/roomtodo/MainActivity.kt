package com.example.roomtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomtodo.ui.theme.RoomTodoTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import com.example.roomtodo.Todo
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private val dao = RoomApplication.db.todoDao()
    private val todoList = mutableStateListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomTodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //val todoList = (1..100).map { "Todo $it" }
                    MainScreen(todoList = todoList)
                }
            }
        }
    }

    private fun loadTodo() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Default) {
                dao.getAll().forEach { todo ->
                    todoList.add(todo)
                }
            }
        }
    }


    private fun postTodo(text: String){
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Default){
                dao.post(Todo(
                    id=0,
                    title = text
                ))
                todoList.clear()
                loadTodo()
            }
        }
    }

    private fun deleteTodo(todo: Todo){
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Default){
                dao.delete(todo)
                todoList.clear()
                loadTodo()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(todoList: SnapshotStateList<Todo>){
        var text: String by remember { mutableStateOf("") }
        // Column
        Column(){
            TopAppBar(
                title = { Text(text = "Todo List") }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ){
                items(todoList){todo ->
                    key(todo.id){
                        TodoItem(todo = todo)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(value = text,
                    onValueChange = { it -> text = it},
                    label = { Text(text = "Todo")},
                    modifier = Modifier.wrapContentHeight().weight(1f)
                )
                Spacer(Modifier.size(16.dp))
                Button(
                    onClick = {
                        if(text.isEmpty()) return@Button
                        postTodo(text)
                        text = ""
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(text = "ADD")
                }
            }
        }
    }

    @Composable
    fun TodoItem(todo: Todo){
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable (onClick = { deleteTodo(todo) })
        ) {
            Text(
                text = todo.title,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
            Text(
                text = "created at: ${sdf.format(todo.created_at)}",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()

            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    RoomTodoTheme {
//        MainScreen()
//    }
//}