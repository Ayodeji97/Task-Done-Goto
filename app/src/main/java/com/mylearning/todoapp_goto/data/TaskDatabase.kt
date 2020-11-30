package com.mylearning.todoapp_goto.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao() : TaskDao

    class Callback @Inject constructor (private val database : Provider<TaskDatabase>, private val applicationScope : CoroutineScope) : RoomDatabase.Callback () {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // database functions

           val dao =  database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Visit Samuel"))
                dao.insert(Task("Go shopping"))
                dao.insert(Task("Sleep after resolving this bug"))
                dao.insert(Task("Do all house chores"))
            }



        }
    }


}