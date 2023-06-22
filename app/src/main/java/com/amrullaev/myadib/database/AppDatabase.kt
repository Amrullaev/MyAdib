package com.amrullaev.myadib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amrullaev.myadib.database.dao.WriterDao
import com.amrullaev.myadib.database.entity.WriterEntity

@Database(entities = [WriterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun writerDao(): WriterDao

    companion object {

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "writer_db")
                .allowMainThreadQueries()
                .build()
        }
    }
}