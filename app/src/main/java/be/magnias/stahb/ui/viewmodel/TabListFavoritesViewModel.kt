package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.TabInfo
import be.magnias.stahb.persistence.TabInfoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabListFavoritesViewModel : ViewModel()
{

    @Inject
    lateinit var tabInfoRepository: TabInfoRepository

    private var allTabs: MutableLiveData<Resource<List<TabInfo>>> = MutableLiveData()

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

    private fun onRetrieveTabsSuccess(tabs : List<TabInfo>){
        allTabs.value = Resource(Status.SUCCESS, tabs, null)
    }

    private fun onRetrieveTabsError(e: Throwable){
        allTabs.value = Resource<List<TabInfo>>(Status.ERROR, null, e.message)
    }

    fun getAllFavoriteTabInfo(): LiveData<Resource<List<TabInfo>>> {
        return allTabs
    }
}