package com.mongmong.namo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mongmong.namo.data.dao.CategoryDao
import com.mongmong.namo.data.dao.DiaryDao
import com.mongmong.namo.data.dao.EventDao
import com.mongmong.namo.data.dao.GroupDao
import com.mongmong.namo.data.entity.diary.Diary
import com.mongmong.namo.data.entity.home.Event
import com.mongmong.namo.data.entity.group.Group
import com.mongmong.namo.data.entity.home.Category

@Database(entities = [Event::class,Group::class, Category::class, Diary::class], version = 1, exportSchema = false)
@TypeConverters(
    value = [
        StringListConverters::class,
        IntListConverters::class,
        EventListConverters::class
    ]
)
abstract class NamoDatabase : RoomDatabase() {
    abstract val eventDao : EventDao
    abstract val diaryDao : DiaryDao
    abstract val groupDao : GroupDao
    abstract val categoryDao : CategoryDao

    companion object {
        @Volatile
        private var INSTANCE : NamoDatabase? = null

        fun getInstance(context : Context) : NamoDatabase {
            synchronized(this){
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NamoDatabase::class.java,
                        "namo_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}