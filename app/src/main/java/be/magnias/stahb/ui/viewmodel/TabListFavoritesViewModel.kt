package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TabListFavoritesViewModel : ViewModel()
{

    @Inject
    lateinit var tabRepository: TabRepository

    private var allTabs: MutableLiveData<Resource<List<Tab>>> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    init{
        App.appComponent.inject(this)


        tabRepository.getFavoriteTabs()
            .onErrorResumeNext(Observable.empty())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .debounce(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe(
                { tabs -> onRetrieveTabsSuccess(tabs) },
                { error -> onRetrieveTabsError(error) }
            )
    }

    private fun onRetrieveTabsSuccess(tabs : List<Tab>){
        allTabs.value = Resource(Status.SUCCESS, tabs, null)
    }

    private fun onRetrieveTabsError(e: Throwable){
//        Logger.e(e.message!!)
        allTabs.value = Resource<List<Tab>>(Status.ERROR, null, e.message)
    }

    fun getFavoriteTabs(): LiveData<Resource<List<Tab>>> {
        return allTabs
    }
}