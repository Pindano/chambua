package com.example.chambua.presentation.Subject

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task
import com.example.chambua.domain.repository.SessionRepo
import com.example.chambua.domain.repository.SubjectRepo
import com.example.chambua.domain.repository.TaskRepo
import com.example.chambua.presentation.navArgs
import com.example.chambua.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectScreenViewModel @Inject constructor(
    private val subjectRepo: SubjectRepo,
    private val taskRepo: TaskRepo,
    private val sessionRepo: SessionRepo,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs: SubjectScreenNav = savedStateHandle.navArgs()

    init {
        getSubject()
    }
    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepo.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepo.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepo.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepo.getTotalSessionsDurationBySubjectId(navArgs.subjectId),
    ){
        state,upcomingTasks,completedTask,recentSessions, totalSessionsDuration->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionsDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    fun onEvent(event: SubjectEvent){
        when(event){
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSession -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SubjectEvent.OnGoalStudyHours -> {
                _state.update{
                    it.copy( goalStudyHours = event.hours)
                }
            }
            is SubjectEvent.OnSubjectColorChange -> {
                _state.update{
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {

                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is SubjectEvent.OnTaskIsComplete -> {updateTask(event.task)}
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull()?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours/goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }
    private fun getSubject(){
        viewModelScope.launch { 
            subjectRepo.getSubjectById(navArgs.subjectId)?.let { 
                subject->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) },
                        currentSubjectId = subject.subjectId

                    )
                }
            }
        }
    }

    private fun updateSubject(){
        viewModelScope.launch {
            subjectRepo.upsertSubject(
                subject = Subject(
                    subjectId = state.value.currentSubjectId,
                    name = state.value.subjectName,
                    goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                    colors = state.value.subjectCardColors.map { it.toArgb() }

                )
            )
        }
    }
    private fun deleteSubject(){
        viewModelScope.launch {
            val currentSubjectId = state.value.currentSubjectId
            if (currentSubjectId != null){
                withContext(Dispatchers.IO){
                    subjectRepo.deleteSubject(currentSubjectId)

                }
            }


        }
    }
    private fun updateTask(task: Task){
        viewModelScope.launch {
            taskRepo.upsertTask(
                task = task.copy(
                    isComplete = !task.isComplete
                )
            )


        }
    }
    private fun deleteSession() {
        viewModelScope.launch {

                state.value.session?.let {
                    sessionRepo.deleteSession(it)

                }

        }
    }

}