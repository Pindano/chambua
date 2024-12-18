package com.example.chambua.data.repository

import com.example.chambua.domain.model.Task
import com.example.chambua.data.local.TaskDao

import com.example.chambua.domain.repository.TaskRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepoImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepo {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        return taskDao.deleteTask(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId)
            .map { tasks -> tasks.filter { it.isComplete.not() } }
            .map { tasks-> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId)
            .map { tasks -> tasks.filter { it.isComplete.not() } }
            .map { tasks-> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { tasks -> tasks.filter { !it.isComplete } }
            .map { tasks-> sortTasks(tasks) }
    }

    private fun sortTasks(tasks: List<Task>): List<Task>{
        return tasks.sortedWith(compareBy<Task>{it.dueDate}.thenByDescending{it.priority})
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

}