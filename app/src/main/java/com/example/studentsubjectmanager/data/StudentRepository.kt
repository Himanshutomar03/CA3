package com.example.studentsubjectmanager.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    val studentsWithSubjects: Flow<List<StudentWithSubjects>> = studentDao.getStudentsWithSubjects()

    suspend fun insertStudent(name: String) {
        studentDao.insertStudent(Student(studentName = name))
    }

    suspend fun addSubject(studentId: Int, subjectName: String): Boolean {
        return studentDao.addSubject(Subject(studentId = studentId, subjectName = subjectName))
    }
}
