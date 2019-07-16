package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TabViewModel(id: String) : ViewModel()
{

    @Inject
    lateinit var tabRepository: TabRepository

    private var tab: MutableLiveData<Tab> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    init{
        App.appComponent.inject(this)

        tabRepository.getTab(id)
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeOn(Schedulers.io())
//            .doOnSubscribe { onRetrieveTabsStart() }
//            .doOnTerminate { onRetrieveTabsFinish() }
            .debounce(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe(
                { tab -> onRetrieveTabsSuccess(tab) },
                { error -> onRetrieveTabsError(error) }
            )
    }

    private fun onRetrieveTabsStart(){

    }

    private fun onRetrieveTabsFinish(){

    }

    private fun onRetrieveTabsSuccess(tab : Tab){
        this.tab.value = tab
    }

    private fun onRetrieveTabsError(e: Throwable){
        Logger.e(e.message!!)
    }

    fun getTab(): LiveData<Tab> {
        return tab
    }

}