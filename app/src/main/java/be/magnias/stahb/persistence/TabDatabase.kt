package be.magnias.stahb.persistence

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import be.magnias.stahb.model.*
import be.magnias.stahb.model.dao.TabDao
import be.magnias.stahb.model.dao.TabViewDao

const val NEW_ID = "new"
const val FAVORITES_ID = "favorites"
const val RECENT_ID = "recent"

@Database(entities = [TabViewTab::class, Tab::class, TabView::class], version = 4, exportSchema = false)
abstract class TabDatabase : RoomDatabase()
{

    abstract fun tabDao() : TabDao

    abstract fun tabViewDao() : TabViewDao

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
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }

        private val roomCallback = object : RoomDatabase.Callback()
        {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance)
                    .execute()
            }
        }
    }

    class PopulateDbAsyncTask(db: TabDatabase?) : AsyncTask<Unit, Unit, Unit>() {
        private val tabViewDao = db?.tabViewDao()

        override fun doInBackground(vararg p0: Unit?) {
            tabViewDao?.insert(TabView(NEW_ID))
            tabViewDao?.insert(TabView(FAVORITES_ID))
            tabViewDao?.insert(TabView(RECENT_ID))
        }
    }
}