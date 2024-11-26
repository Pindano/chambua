package com.example.chambua.presentation.Subject

import androidx.compose.ui.graphics.Color
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task
import com.example.chambua.presentation.dashboard.DashboardEvent

data class SubjectState(
    val currentSubjectId: Int? =null,
    val subjectName: String = "",
    val goalStudyHours:String = "",
    val studiedHours: Float = 0f,
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null,
    val progress: Float = 0f,


)

