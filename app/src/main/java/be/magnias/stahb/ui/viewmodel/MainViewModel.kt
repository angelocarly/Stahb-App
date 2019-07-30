package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.persistence.TabRepository
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var tabRepository: TabRepository

    private val refreshLoadingVisibility = MutableLiveData<Resource<Boolean>>()

    private var refreshSubscription: Disposable? = null

    init {
        App.appComponent.inject(this)
    }

    fun getRefreshLoadingVisibility(): LiveData<Resource<Boolean>> {
        return refreshLoadingVisibility
    }

    fun refreshTabs() {
        if(refreshSubscription != null) refreshSubscription!!.dispose()
        refreshSubscription = tabRepository.refresh()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                refreshLoadingVisibility.value = Resource(Status.SUCCESS, true, null)
                Logger.d("Refreshed tabs")
            }
            .subscribe(
                { result ->  },
                { error ->
                    refreshLoadingVisibility.value = Resource(Status.ERROR, true, error.message)
                    Logger.e(error.message!!)
                }
            )
    }

    override fun onCleared() {
        super.onCleared()
        if(refreshSubscription != null) refreshSubscription!!.dispose()
    }
}