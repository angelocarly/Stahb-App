package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.service.UserService
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * The viewModel for the Login Fragment.
 * Provides a way for the UI to login in a asynchronous way.
 */
class LoginViewModel : ViewModel() {

    @Inject
    lateinit var userService: UserService

    /**
     * Provides the status of the request
     */
    private var loginResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()

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
    }

    private fun onLoginStart() {
        // Set the loading status to loading
        loadingVisibility.value = true
    }

    private fun onLoginFinish() {
        // Set the loading status to not loading
        loadingVisibility.value = false
    }

    private fun onLoginSuccess() {
        // Set the result to a Success status
        this.loginResult.value = Resource(Status.SUCCESS, null, null)
    }

    private fun onLoginError(e: Throwable) {
        // Set the result to an Error status
        if (e is UnAuthorizedException) this.loginResult.value = Resource(Status.ERROR, null, "Incorrect Login Credentials")
        else this.loginResult.value = Resource(Status.ERROR, null, "Bad request")
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getLoginResult(): LiveData<Resource<Boolean>> {
        return loginResult
    }

    /**
     * Try to log a user in.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    fun login(username: String, password: String) {
        // Dispose the previous request
        disposable.clear()

        // Try to log the user in
        disposable.add(userService.login(username, password)
            .doOnSubscribe { onLoginStart() }
            .subscribe(
                { onLoginSuccess(); onLoginFinish() },
                { error -> onLoginError(error); onLoginFinish() }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()

        //Dispose the login request
        disposable.dispose()
    }

}