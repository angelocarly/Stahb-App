package be.magnias.stahb.persistence

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import be.magnias.stahb.model.*
import be.magnias.stahb.model.dao.TabDao

@Database(entities = [Tab::class], version = 8, exportSchema = false)
abstract class TabDatabase : RoomDatabase()
{

    abstract fun tabDao() : TabDao

    companion object {

        @Volatile
        private var instance: TabDatabase? = null

        fun getInstance(context: Context): TabDatabase {
            if (instance == null) {
                synchronized(TabDatabase::class)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TabDatabase::class.java, "tab_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }
    }
}