package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.service.TabService
import be.magnias.stahb.service.UserService
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The viewModel for the MainActivity
 * Provides access to the UI for general service calls.
 */
class MainViewModel : ViewModel() {

    @Inject
    lateinit var tabService: TabService

    @Inject
    lateinit var userService: UserService

    /**
     * Indicates whether the loading view should be displayed.
     */
    private val refreshLoadingVisibility = MutableLiveData<Resource<Boolean>>()

    /**
     * Disposable to dispose of requests
     */
    private var refreshSubscription: CompositeDisposable = CompositeDisposable()

    init {
        // Inject services with Dagger
        App.appComponent.inject(this)
    }

    /**
     * @return a livedata object which shows when the data is loaded or an error occurred.
     */
    fun getRefreshLoadingVisibility(): LiveData<Resource<Boolean>> {
        return refreshLoadingVisibility
    }

    /**
     * Refresh the available tabs with their latest online version.
     */
    fun refreshTabs() {
        refreshSubscription.clear()
        refreshSubscription.add(
            tabService.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate {
                    refreshLoadingVisibility.value = Resource(Status.SUCCESS, true, null)
                    Logger.d("Refreshed tabs")
                }
                .doOnError {
                    refreshLoadingVisibility.value = Resource(Status.ERROR, true, it.message)
                }
                .subscribe(
                    { },
                    { error ->
                        Logger.e(error.message!!)
                    }
                )
        )
    }

    /**
     * Log the user out.
     */
    fun logout() {
        userService.logout()
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of any requests
        refreshSubscription.dispose()
    }
}