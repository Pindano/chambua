package com.example.chambua.domain.repository

import com.example.chambua.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepo {
    suspend fun upsertSubject(subject: Subject)
    fun getTotalSubjectCount(): Flow<Int>
    fun getTotalGoalHours(): Flow<Float>
    suspend fun deleteSubject(subjectId: Int)
    suspend fun getSubjectById(subjectId: Int): Subject?

    fun getAllSubjects(): Flow<List<Subject>>
}