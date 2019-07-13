package be.magnias.stahb.ui

import androidx.lifecycle.MutableLiveData
import be.magnias.stahb.model.Tab
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class TabViewModel : BaseViewModel()
{

    @Inject
    lateinit var stahbApi: StahbApi

    private lateinit var subscription: Disposable

    private var allTabs: MutableLiveData<List<Tab>> = MutableLiveData<List<Tab>>()

    /**
     * Indicates whether the loading view should be displayed.
     */
    val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    init{
        loadTabs()
    }

    private fun loadTabs(){
        subscription = stahbApi.getTabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveTabsStart() }
            .doOnTerminate { onRetrieveTabsFinish() }
            .subscribe(
                { result -> onRetrieveTabsSuccess(result) },
                { error -> onRetrieveTabsError(error) }
            )
    }

    private fun onRetrieveTabsError(error: Throwable) {
        Logger.e(error.message!!)
    }
    private fun onRetrieveTabsSuccess(result: List<Tab>) {
        Logger.d("Loaded ${result.size} tabs")
        allTabs.value = result

    }
    private fun onRetrieveTabsFinish() {
        Logger.i("Finished loading tab list")
        loadingVisibility.value = false
    }
    private fun onRetrieveTabsStart() {
        Logger.i("Started loading tab list")
        loadingVisibility.value = true
    }

    /**
     * Disposes the subscription when the [BaseViewModel] is
    no longer used.
     */
    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun getAllTabs(): MutableLiveData<List<Tab>> {
        return allTabs
    }

}