package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.studentsubjectmanager.ui.StudentScreen
import com.example.studentsubjectmanager.ui.StudentViewModel
import com.example.studentsubjectmanager.ui.StudentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            StudentViewModelFactory(application)
        )[StudentViewModel::class.java]

        setContent {
            MyApplicationTheme {
                StudentScreen(viewModel = viewModel)
            }
        }
    }
}
