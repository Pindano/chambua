package com.example.chambua.di

import android.app.Application
import androidx.room.Room
import com.example.chambua.data.local.AppDatabase
import com.example.chambua.data.local.SessionDao
import com.example.chambua.data.local.SubjectDao
import com.example.chambua.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDatabase{
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "chambua.db"
        ).build()
    }


    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao{
        return database.subjectDao()
    }
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao{
        return database.taskDao()
    }
    @Provides
    @Singleton
    fun provideDao(database: AppDatabase): SessionDao{
        return database.sessionDao()
    }

}