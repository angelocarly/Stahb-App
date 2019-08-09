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
import com.orhanobut.logger.Logger
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
    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    private var subscription: Disposable

    init{
        App.appComponent.inject(this)

        subscription = tabRepository.getFavoriteTabs()
            .debounce(700, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .doOnSubscribe{onRetrieveTabsStart()}
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

    private fun onRetrieveTabsSuccess(tabs : List<Tab>){
        allTabs.value = Resource(Status.SUCCESS, tabs, null)
    }

    private fun onRetrieveTabsError(e: Throwable){
        allTabs.value = Resource<List<Tab>>(Status.ERROR, null, e.message)
    }

    fun getAllFavoriteTabInfo(): LiveData<Resource<List<Tab>>> {
        return allTabs
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}