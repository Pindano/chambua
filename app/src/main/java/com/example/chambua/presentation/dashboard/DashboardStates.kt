package com.example.chambua.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject

data class DashboardStates(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalsStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val session: Session? = null

)