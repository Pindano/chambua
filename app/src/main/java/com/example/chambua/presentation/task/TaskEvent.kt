package com.example.chambua.presentation.task

import com.example.chambua.domain.model.Subject
import com.example.chambua.util.Priority

sealed class TaskEvent {
    data class OnTitleChange(val title: String): TaskEvent()
    data class OnDescriptionChange(val description: String):TaskEvent()
    data class OnDateChange(val millis: Long?):TaskEvent()
    data class OnPriorityChange(val priority: Priority): TaskEvent()
    data class OnRelatedSubject(val subject: Subject):TaskEvent()
    data object OnIsComplete:TaskEvent()
    data object SaveTask: TaskEvent()
    data object DeleteTask: TaskEvent()

}