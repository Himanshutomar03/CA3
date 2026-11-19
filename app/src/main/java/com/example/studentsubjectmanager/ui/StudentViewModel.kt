package com.example.studentsubjectmanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studentsubjectmanager.data.AppDatabase
import com.example.studentsubjectmanager.data.StudentRepository
import com.example.studentsubjectmanager.data.StudentWithSubjects
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    val studentsWithSubjects: StateFlow<List<StudentWithSubjects>>

    init {
        val studentDao = AppDatabase.getDatabase(application).studentDao()
        repository = StudentRepository(studentDao)
        studentsWithSubjects = repository.studentsWithSubjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addStudent(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertStudent(name)
        }
    }

    fun addSubject(studentId: Int, subjectName: String, onResult: (Boolean) -> Unit) {
        if (subjectName.isBlank()) return
        viewModelScope.launch {
            val success = repository.addSubject(studentId, subjectName)
            onResult(success)
        }
    }
}

class StudentViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
