package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabViewRepository
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabListViewModel : ViewModel() {

    @Inject
    lateinit var tabViewRepository: TabViewRepository

    private var allTabs: MutableLiveData<Resource<List<Tab>>> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    private val refreshLoadingVisibility = MutableLiveData<Resource<Boolean>>()

    private var subscription: Disposable
    private var refreshSubscription: Disposable? = null

    init {
        App.appComponent.inject(this)

        subscription = tabViewRepository.getAllTabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .doOnSubscribe { onRetrieveTabsStart() }
            .subscribe(
                { tabs -> onRetrieveTabsSuccess(tabs); onRetrieveTabsFinish() },
                { error -> onRetrieveTabsError(error); onRetrieveTabsFinish() }
            )
    }

    private fun onRetrieveTabsStart() {
        loadingVisibility.value = View.VISIBLE
    }

    private fun onRetrieveTabsFinish() {
        loadingVisibility.value = View.GONE
    }

    private fun onRetrieveTabsSuccess(tabs: List<Tab>) {
        allTabs.value = Resource(Status.SUCCESS, tabs, null)
    }

    private fun onRetrieveTabsError(e: Throwable) {
        allTabs.value = Resource<List<Tab>>(Status.ERROR, null, e.message)
    }

    fun getLoadingVisibility(): LiveData<Int> {
        return loadingVisibility
    }

    fun getAllTabInfo(): LiveData<Resource<List<Tab>>> {
        return allTabs
    }

    fun getRefreshLoadingVisibility(): LiveData<Resource<Boolean>> {
        return refreshLoadingVisibility
    }

    fun refreshTabs() {
        if(refreshSubscription != null) refreshSubscription!!.dispose()
        refreshSubscription = tabViewRepository.refreshAllTabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .firstOrError()
            .subscribe(
                { refreshLoadingVisibility.value = Resource(Status.SUCCESS, true, null) },
                { error -> refreshLoadingVisibility.value = Resource(Status.ERROR, true, error.message) }
            )
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
        if(refreshSubscription != null) refreshSubscription!!.dispose()
    }
}