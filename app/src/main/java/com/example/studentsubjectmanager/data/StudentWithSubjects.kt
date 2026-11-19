package com.example.studentsubjectmanager.data

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithSubjects(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "studentId",
        entityColumn = "studentId"
    )
    val subjects: List<Subject>
)
