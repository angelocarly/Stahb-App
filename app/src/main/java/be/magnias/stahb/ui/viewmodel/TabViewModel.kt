package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabRepository
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for the Tab Fragment.
 * Provides a way for the UI to access the Tab.
 * @param id The id of the requested tab.
 */
class TabViewModel(id: String) : ViewModel() {

    private val tabId = id

    @Inject
    lateinit var tabRepository: TabRepository

    /**
     * Provides the tab
     */
    private var tab: MutableLiveData<Resource<Tab>> = MutableLiveData()

    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Disposable to dispose of requests
     */
    private var disposable = CompositeDisposable()

    init {
        // Inject services with Dagger
        App.appComponent.inject(this)

        // Get the tab
        disposable.add(
            tabRepository.getTab(id)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onRetrieveTabStart() }
                .debounce(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(
                    { tab -> onRetrieveTabsSuccess(tab); onRetrieveTabFinish() },
                    { error -> onRetrieveTabsError(error); onRetrieveTabFinish() }
                )
        )
    }

    private fun onRetrieveTabStart() {
        // Set the loading status to visible
        loadingVisibility.value = true
    }

    private fun onRetrieveTabFinish() {
        // Set the loading status to invisible
        loadingVisibility.value = false
    }

    private fun onRetrieveTabsSuccess(tab: Tab) {
        // Set the result to the received tab
        this.tab.value = Resource(Status.SUCCESS, tab, null)
    }

    private fun onRetrieveTabsError(e: Throwable) {
        // Set the result to an Error Status
        this.tab.value = Resource(Status.ERROR, null, e.message)
        Logger.e(e.message!!)
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getTab(): LiveData<Resource<Tab>> {
        return tab
    }

    /**
     * Add the current tab to the user's favorites.
     */
    fun addToFavorite() {
        tabRepository.addFavorite(tabId)
    }

    /**
     * Remove the current tab from the user's favorites.
     */
    fun removeFromFavorite() {
        tabRepository.removeFromFavorites(tabId)
    }

    override fun onCleared() {
        super.onCleared()

        // Dispose of the request
        disposable.dispose()
    }

}