package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.service.TabService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for the TabList Fragment.
 * Provides a way for the UI to receive all the tabs.
 */
class TabListViewModel : ViewModel() {

    @Inject
    lateinit var tabService: TabService

    /**
     * Provides the loaded tabs
     */
    private var allTabs: MutableLiveData<Resource<List<Tab>>> = MutableLiveData()

    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    /**
     * Disposable to dispose of requests
     */
    private var subscription: Disposable

    init {
        // Inject services with Dagger
        App.appComponent.inject(this)

        // Load the tabs
        subscription = tabService.getAllTabs()
            .debounce(700, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .doOnSubscribe { onRetrieveTabsStart() }
            .subscribe(
                { tabs -> onRetrieveTabsSuccess(tabs); onRetrieveTabsFinish() },
                { error -> onRetrieveTabsError(error); onRetrieveTabsFinish() }
            )
    }

    private fun onRetrieveTabsStart() {
        // Set the loading status to visible
        loadingVisibility.value = View.VISIBLE
    }

    private fun onRetrieveTabsFinish() {
        // Set the loading status to invisible
        loadingVisibility.value = View.GONE
    }

    private fun onRetrieveTabsSuccess(tabs: List<Tab>) {
        // Store the received tabs in the result
        allTabs.value = Resource(Status.SUCCESS, tabs, null)
    }

    private fun onRetrieveTabsError(e: Throwable) {
        // Store an Error Status in the result
        allTabs.value = Resource<List<Tab>>(Status.ERROR, null, e.message)
    }

    fun getLoadingVisibility(): LiveData<Int> {
        return loadingVisibility
    }

    fun getAllTabInfo(): LiveData<Resource<List<Tab>>> {
        return allTabs
    }

    override fun onCleared() {
        super.onCleared()

        //Dispose of the request
        subscription.dispose()
    }
}