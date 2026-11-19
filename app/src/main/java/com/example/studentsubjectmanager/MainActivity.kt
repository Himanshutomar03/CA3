package com.example.studentsubjectmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.lifecycle.lifecycleScope
import com.example.studentsubjectmanager.data.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dao: StudentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "student_db"
        ).build()

        dao = db.studentDao()

        setContent {
            MaterialTheme {
                StudentScreen(dao)
            }
        }
    }
}

@Composable
fun StudentScreen(dao: StudentDao) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var studentName by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val students by dao.getStudentsWithSubjects()
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Student Subject Manager",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Student form
        Text("Add Student")

        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text("Student Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (studentName.isNotBlank()) {

                    scope.launch {
                        dao.insertStudent(
                            Student(studentName = studentName)
                        )

                        studentName = ""

                        Toast.makeText(
                            context,
                            "Student Added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Student")
        }

        HorizontalDivider()

        // Subject form
        Text("Add Subject")

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = subjectName,
            onValueChange = { subjectName = it },
            label = { Text("Subject Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val id = studentId.toIntOrNull()

                if (id == null || subjectName.isBlank()) {
                    Toast.makeText(
                        context,
                        "Enter valid Student ID and Subject",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    scope.launch {

                        val result = dao.addSubject(
                            Subject(
                                subjectName = subjectName,
                                studentId = id
                            )
                        )

                        if (result) {
                            Toast.makeText(
                                context,
                                "Subject Added",
                                Toast.LENGTH_SHORT
                            ).show()

                            subjectName = ""
                        } else {
                            Toast.makeText(
                                context,
                                "Maximum 3 subjects reached!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Subject")
        }

        HorizontalDivider()

        Text(
            text = "Student Records",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn {
            items(students) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            "ID: ${item.student.studentId}"
                        )

                        Text(
                            "Name: ${item.student.studentName}"
                        )

                        Text("Subjects:")

                        item.subjects.forEach {
                            Text("• ${it.subjectName}")
                        }

                        Text(
                            "${item.subjects.size}/3 Subjects"
                        )
                    }
                }
            }
        }
    }
}
