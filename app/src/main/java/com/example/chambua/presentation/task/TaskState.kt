package com.example.chambua.presentation.task

import com.example.chambua.domain.model.Subject
import com.example.chambua.util.Priority

data class TaskState (
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.Low,
    val relatedToSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId: Int? = null

    )