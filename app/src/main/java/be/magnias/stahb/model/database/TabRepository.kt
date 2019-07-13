package be.magnias.stahb.model.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabDao
import be.magnias.stahb.network.StahbApi
import javax.inject.Inject

class TabRepository(application: Application)
{

    @Inject
    lateinit var stahbApi: StahbApi

    private var tabDao: TabDao

    private var allTabs: LiveData<List<Tab>>

    init
    {
        val database: TabDatabase = TabDatabase.getInstance(
            application.applicationContext
        )!!
        tabDao = database.tabDao()
        allTabs = tabDao.getAllTabs()
    }

    fun insert(tab : Tab) {
        val insertTabAsyncTask = InsertTabAsyncTask(tabDao).execute(tab)
    }

    fun update(tab : Tab) {
        val updateTabAsyncTask = UpdateTabAsyncTask(tabDao).execute(tab)
    }

    fun delete(tab : Tab) {
        val deleteTabAsyncTask = DeleteTabAsyncTask(tabDao).execute(tab)
    }

    fun getAllTabs(): LiveData<List<Tab>> {
        return allTabs
    }

    companion object
    {
        private class InsertTabAsyncTask(tabDao: TabDao) : AsyncTask<Tab, Unit, Unit>() {
            val tabDao = tabDao

            override fun doInBackground(vararg p0: Tab?) {
                tabDao.insert(p0[0]!!)
            }
        }

        private class UpdateTabAsyncTask(tabDao: TabDao) : AsyncTask<Tab, Unit, Unit>() {
            val tabDao = tabDao

            override fun doInBackground(vararg p0: Tab?) {
                tabDao.update(p0[0]!!)
            }
        }

        private class DeleteTabAsyncTask(tabDao: TabDao) : AsyncTask<Tab, Unit, Unit>() {
            val tabDao = tabDao

            override fun doInBackground(vararg p0: Tab?) {
                tabDao.delete(p0[0]!!)
            }
        }
    }
}