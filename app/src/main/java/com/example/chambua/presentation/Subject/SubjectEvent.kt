package com.example.chambua.presentation.Subject

import androidx.compose.ui.graphics.Color
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Task

sealed class SubjectEvent {
    data object UpdateSubject: SubjectEvent()
    data object DeleteSubject: SubjectEvent()
    data object DeleteSession: SubjectEvent()
    data object UpdateProgress: SubjectEvent()
    data class OnTaskIsComplete(val task: Task): SubjectEvent()
    data class OnSubjectColorChange(val color: List<Color>): SubjectEvent()
    data class OnSubjectNameChange(val name: String): SubjectEvent()
    data class OnGoalStudyHours(val hours: String): SubjectEvent()
    data class OnDeleteSession(val session: Session): SubjectEvent()
}