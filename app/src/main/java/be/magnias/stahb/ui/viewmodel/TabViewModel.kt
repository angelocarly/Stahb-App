package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TabViewModel(id: String) : ViewModel() {

    val tabId = id

    @Inject
    lateinit var tabRepository: TabRepository

    private var tab: MutableLiveData<Resource<Tab>> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    private var disposable = CompositeDisposable()

    init {

        App.appComponent.inject(this)

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
        loadingVisibility.value = true
    }

    private fun onRetrieveTabFinish() {
        loadingVisibility.value = false
    }

    private fun onRetrieveTabsSuccess(tab: Tab) {
        this.tab.value = Resource(Status.SUCCESS, tab, null)
    }

    private fun onRetrieveTabsError(e: Throwable) {
        this.tab.value = Resource(Status.ERROR, null, e.message)
        Logger.e(e.message!!)
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getTab(): LiveData<Resource<Tab>> {
        return tab
    }

    fun addToFavorite() {
        disposable.add(tabRepository.addFavorite(tabId))
    }

    fun removeFromFavorite() {
        disposable.add(tabRepository.removeFromFavorites(tabId))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}