package com.example.chambua.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Task
import com.example.chambua.presentation.task.TaskEvent

sealed class DashboardEvent{
    data object SaveSubject: DashboardEvent()

    data object DeleteSession: DashboardEvent()

    data class onDeleteSessionClick(val session: Session): DashboardEvent()

    data class OnTaskIsCompleteChange(val task: Task): DashboardEvent()


    data class onSubjectColorChange(val colors: List<Color>): DashboardEvent()

    data class onSubjectNameChange(val name: String): DashboardEvent()

    data class onGoalStudyHoursChange(val hours:String): DashboardEvent()
}