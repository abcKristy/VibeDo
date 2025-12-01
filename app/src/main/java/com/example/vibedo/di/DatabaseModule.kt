package com.example.vibedo.di

import android.content.Context
import androidx.room.Room
import com.example.vibedo.model.TaskDao
import com.example.vibedo.model.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase{
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "todo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun providetaskDao(database: TaskDatabase): TaskDao{
        return database.taskDao()
    }
}