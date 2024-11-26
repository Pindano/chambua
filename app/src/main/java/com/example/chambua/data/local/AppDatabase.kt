package com.example.chambua.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chambua.domain.model.Session
import com.example.chambua.domain.model.Subject
import com.example.chambua.domain.model.Task

@Database(
    entities = [Subject::class,Session::class, Task::class ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao
}