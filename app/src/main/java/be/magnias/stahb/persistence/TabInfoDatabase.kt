package be.magnias.stahb.persistence

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabDao
import be.magnias.stahb.model.TabInfo
import be.magnias.stahb.model.TabInfoDao

@Database(entities = [TabInfo::class], version = 1, exportSchema = false)
abstract class TabInfoDatabase : RoomDatabase()
{

    abstract fun tabInfoDao() : TabInfoDao

    companion object {

        @Volatile
        private var instance: TabInfoDatabase? = null

        fun getInstance(context: Context): TabInfoDatabase {
            if (instance == null) {
                synchronized(TabInfoDatabase::class)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TabInfoDatabase::class.java, "tab_info_database"
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