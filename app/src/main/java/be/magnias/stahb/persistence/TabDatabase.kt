package be.magnias.stahb.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import be.magnias.stahb.model.*
import be.magnias.stahb.persistence.dao.TabDao

/**
 * Configuration class for the Room database.
 */
@Database(entities = [Tab::class], version = 9, exportSchema = false)
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