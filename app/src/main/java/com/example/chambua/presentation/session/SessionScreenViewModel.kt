package com.example.chambua.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.repository.SessionRepo
import com.example.chambua.domain.repository.SubjectRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class SessionScreenViewModel @Inject constructor(
    subjectRepository: SubjectRepo,
    private val sessionRepository: SessionRepo
):ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ) { state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SessionState()
    )



    fun onEvent(event: SessionEvent) {
        when (event) {
            SessionEvent.DeleteSession -> deleteSession()
            is SessionEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is SessionEvent.SaveSession -> insertSession(event.duration)
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedToSubject,
                        subjectId = event.subjectId
                    )
                }
            }
            else -> TODO()
        }
    }



    private fun deleteSession() {
        viewModelScope.launch {
            state.value.session?.let {
                sessionRepository.deleteSession(it)
            }
        }
    }

    private fun insertSession(duration: Long) {
        viewModelScope.launch {
            sessionRepository.insertSession(
                session = Session(
                    sessionSubjectId = state.value.subjectId ?: -1,
                    relatedToSubject = state.value.relatedToSubject ?: "",
                    date = Instant.now().toEpochMilli(),
                    duration = duration
                )
            )

        }
    }
}


