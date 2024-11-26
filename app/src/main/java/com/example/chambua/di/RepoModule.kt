package com.example.chambua.di


import com.example.chambua.data.repository.SessionRepoImpl
import com.example.chambua.data.repository.SubjectRepoImpl
import com.example.chambua.data.repository.TaskRepoImpl
import com.example.chambua.domain.repository.SessionRepo
import com.example.chambua.domain.repository.SubjectRepo
import com.example.chambua.domain.repository.TaskRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepo(
        impl: SubjectRepoImpl
    ): SubjectRepo

    @Singleton
    @Binds
    abstract fun bindTaskRepo(
        impl: TaskRepoImpl
    ): TaskRepo

    @Singleton
    @Binds
    abstract fun bindSessionRepo(
        impl: SessionRepoImpl
    ): SessionRepo
}