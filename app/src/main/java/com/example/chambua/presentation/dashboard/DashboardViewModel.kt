package com.example.chambua.presentation.dashboard

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task
import com.example.chambua.domain.repository.SessionRepo
import com.example.chambua.domain.repository.SubjectRepo
import com.example.chambua.domain.repository.TaskRepo
import com.example.chambua.presentation.task.TaskEvent
import com.example.chambua.util.SnackBarEvent
import com.example.chambua.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepo: SubjectRepo,
    private val SessionRepo: SessionRepo,
    private val taskRepo: TaskRepo

): ViewModel(){

    private val _state = MutableStateFlow(DashboardStates())
    val state = combine(
        _state,
        subjectRepo.getAllSubjects(),
        subjectRepo.getTotalSubjectCount(),
        subjectRepo.getTotalGoalHours(),
        SessionRepo.getTotalSessionsDuration()
    ){state, subjects, subjectCount, goalHours,totalSessionDuration ->
    state.copy(
        totalSubjectCount = subjectCount,
        totalGoalsStudyHours = goalHours,
        subjects = subjects,
        totalStudiedHours = totalSessionDuration.toHours()

    )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStates()
    )

    val tasks: StateFlow<List<Task>> =  taskRepo.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()

        )
    val recentSessions: StateFlow<List<Session>> =  SessionRepo.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()

        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    fun onEvent(event: DashboardEvent){
        when(event){
            is DashboardEvent.onSubjectNameChange->{
                _state.update{
                    it.copy(subjectName = event.name)
                }

            }
            is DashboardEvent.onGoalStudyHoursChange->{
                _state.update{
                    it.copy(goalStudyHours = event.hours)
                }

            }
            is DashboardEvent.onSubjectColorChange->{
                _state.update{
                    it.copy(subjectCardColors = event.colors)
                }

            }
            is DashboardEvent.onDeleteSessionClick->{
                _state.update{
                    it.copy( session= event.session)
                }

            }

            DashboardEvent.SaveSubject -> saveSubject()
            DashboardEvent.DeleteSession -> {}

            is DashboardEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
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
    private fun saveSubject(){
        viewModelScope.launch {
            try{
                subjectRepo.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb()}
                    )
                )
                _state.update{
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )
                }
                _snackbarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Subject saved successfully"
                    )
                )
            } catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Couldn't Save subject"
                    )
                )
            }

        }
    }

}