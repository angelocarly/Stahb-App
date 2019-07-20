package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabViewRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabListFavoritesViewModel : ViewModel()
{

    @Inject
    lateinit var tabInfoRepository: TabViewRepository

    private var allTabs: MutableLiveData<Resource<List<Tab>>> = MutableLiveData()

    /**
     * Indicates whether the loading view should be displayed.
     */
    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    init{
        App.appComponent.inject(this)


        tabInfoRepository.getFavoriteTabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
//            .debounce(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .doOnSubscribe{onRetrieveTabsStart()}
            .doOnTerminate{onRetrieveTabsFinish()}
            .subscribe(
                { tabs -> onRetrieveTabsSuccess(tabs) },
                { error -> onRetrieveTabsError(error) }
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
}