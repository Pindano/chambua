package com.example.chambua.data.repository

import com.example.chambua.data.local.SessionDao
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.repository.SessionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepoImpl @Inject constructor(
    private val sessionDao: SessionDao
): SessionRepo {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        return sessionDao.deleteSession(session)
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().take(count = 5)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
return sessionDao.getRecentSessionsForSubject(subjectId).take(count = 10)
        }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
       return sessionDao.getTotalSessionsDuration()
    }

    override fun getTotalSessionsDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionsDurationBySubjectId(subjectId)
    }

    override fun deleteSessionsBySubjectId(subjectId: Int) {
        TODO("Not yet implemented")
    }
}