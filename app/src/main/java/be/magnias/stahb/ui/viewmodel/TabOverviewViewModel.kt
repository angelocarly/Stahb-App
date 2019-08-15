package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.service.IUserService
import be.magnias.stahb.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for the Overview Fragment.
 * Provides a way for the UI to access the loggedInStatus of users.
 */
class TabOverviewViewModel : ViewModel() {

    @Inject
    lateinit var userService: IUserService

    /**
     * Provides the user status
     */
    private var isUserLoggedIn = MutableLiveData<Boolean>()

    /**
     * Disposable to dispose of requests
     */
    private var subscription: Disposable

    init {
        // Inject services with Dagger
        App.appComponent.inject(this)

        // Get the logged in status
        subscription = userService.getUserLoggedIn()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
            isUserLoggedIn.postValue(it)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return userService.isUserLoggedIn()
    }

    fun getUserLoggedIn(): LiveData<Boolean> {
        return isUserLoggedIn
    }

    override fun onCleared() {
        subscription.dispose()

        // Dispose of the request
        super.onCleared()
    }
}