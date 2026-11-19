package com.example.studentsubjectmanager.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studentsubjectmanager.data.StudentWithSubjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val context = LocalContext.current
    val studentsWithSubjects by viewModel.studentsWithSubjects.collectAsState()

    var studentNameInput by remember { mutableStateOf("") }
    var selectedStudentForSubject by remember { mutableStateOf<StudentWithSubjects?>(null) }
    var subjectNameInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Subject Manager") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Add Student Section
            Text("Add New Student", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = studentNameInput,
                    onValueChange = { studentNameInput = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (studentNameInput.isNotBlank()) {
                            viewModel.addStudent(studentNameInput)
                            studentNameInput = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Students List (Max 3 Subjects per Student)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Students List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(studentsWithSubjects) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Student: ${item.student.studentName} (ID: ${item.student.studentId})",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            if (item.subjects.isEmpty()) {
                                Text(
                                    text = "No subjects added yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Subjects (${item.subjects.size}/3):",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                item.subjects.forEach { subject ->
                                    Text(
                                        text = "• ${subject.subjectName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    selectedStudentForSubject = item
                                    subjectNameInput = ""
                                },
                                enabled = item.subjects.size < 3
                            ) {
                                Text(if (item.subjects.size < 3) "Add Subject" else "Subject Limit Reached (3/3)")
                            }
                        }
                    }
                }
            }
        }

        // Add Subject Dialog
        selectedStudentForSubject?.let { studentItem ->
            AlertDialog(
                onDismissRequest = { selectedStudentForSubject = null },
                title = { Text("Add Subject for ${studentItem.student.studentName}") },
                text = {
                    OutlinedTextField(
                        value = subjectNameInput,
                        onValueChange = { subjectNameInput = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (subjectNameInput.isNotBlank()) {
                                viewModel.addSubject(
                                    studentId = studentItem.student.studentId,
                                    subjectName = subjectNameInput
                                ) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Subject added!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed: Maximum 3 subjects allowed!", Toast.LENGTH_LONG).show()
                                    }
                                }
                                selectedStudentForSubject = null
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedStudentForSubject = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
