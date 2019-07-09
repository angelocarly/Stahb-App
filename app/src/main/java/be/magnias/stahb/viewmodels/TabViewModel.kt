package be.magnias.stahb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import be.magnias.stahb.data.Tab
import be.magnias.stahb.data.TabRepository

class TabViewModel(application: Application) : AndroidViewModel(application)
{

    private var repository: TabRepository = TabRepository(application)
    private var allTabs: LiveData<List<Tab>> = repository.getAllTabs()

    fun insert(tab: Tab)
    {
        repository.insert(tab)
    }

    fun update(tab: Tab)
    {
        repository.update(tab)
    }

    fun delete(tab: Tab)
    {
        repository.delete(tab)
    }

    fun getAllTabs() : LiveData<List<Tab>>
    {
        return allTabs
    }

}