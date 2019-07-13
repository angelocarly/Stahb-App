package be.magnias.stahb.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.database.TabRepository
import be.magnias.stahb.network.StahbApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.logging.Logger
import javax.inject.Inject

class TabViewModel : BaseViewModel()
{

    @Inject
    lateinit var stahbApi: StahbApi

    private lateinit var subscription: Disposable

    private var allTabs: MutableLiveData<List<Tab>> = MutableLiveData<List<Tab>>()

    init{
        loadTabs()
    }

    private fun loadTabs(){
        subscription = stahbApi.getTabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveTabsStart() }
            .doOnTerminate { onRetrieveTabsFinish() }
            .subscribe(
                { result -> onRetrieveTabsSuccess(result) },
                { error -> onRetrieveTabsError(error) }
            )
    }

    private fun onRetrieveTabsError(error: Throwable) {
        Log.e("Stahb", error.message!!)
    }
    private fun onRetrieveTabsSuccess(result: List<Tab>) {

        Log.i("Stahb", "loaded")
        allTabs.value = result

    }
    private fun onRetrieveTabsFinish() {
//        loadingVisibility.value = View.GONE
    }
    private fun onRetrieveTabsStart() {
//        loadingVisibility.value = View.VISIBLE
    }

    fun getAllTabs() : LiveData<List<Tab>>
    {
        return allTabs
    }

}