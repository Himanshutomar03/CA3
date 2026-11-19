package com.example.studentsubjectmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    // Insert student
    @Insert
    suspend fun insertStudent(student: Student)

    // Insert subject
    @Insert
    suspend fun insertSubject(subject: Subject)

    // Count subjects of a student
    @Query("SELECT COUNT(*) FROM subjects WHERE studentId = :id")
    suspend fun getSubjectCount(id: Int): Int

    // Check limit before inserting subject
    @Transaction
    suspend fun addSubject(subject: Subject): Boolean {

        val count = getSubjectCount(subject.studentId)

        return if (count < 3) {
            insertSubject(subject)
            true
        } else {
            false
        }
    }

    // Get students with their subjects
    @Transaction
    @Query("SELECT * FROM students")
    fun getStudentsWithSubjects():
        Flow<List<StudentWithSubjects>>
}
