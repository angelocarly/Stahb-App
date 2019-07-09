package be.magnias.stahb.data

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Tab::class], version = 1)
abstract class TabDatabase : RoomDatabase()
{

    abstract fun tabDao() : TabDao

    companion object
    {

        private var instance: TabDatabase? = null

        fun getInstance(context: Context): TabDatabase?
        {
            if(instance == null)
            {
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
            return instance
        }

        fun destroyInstance()
        {
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
        private val noteDao = db?.tabDao()

        override fun doInBackground(vararg p0: Unit?) {
            noteDao?.insert(Tab("Artist 1", "Song 1", "ik", "q"))
            noteDao?.insert(Tab("Artist 2", "Song 2", "jij", "em"))
            noteDao?.insert(Tab("Artist 3", "Song 3", "lol", "abc"))
        }
    }

}