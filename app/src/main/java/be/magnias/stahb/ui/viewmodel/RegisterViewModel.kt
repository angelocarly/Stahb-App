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
 * ViewModel for the Register Fragment.
 * Provides a way for the UI to register new users.
 */
class RegisterViewModel : ViewModel() {

    @Inject
    lateinit var userService: UserService

    /**
     * Provides the status of the request
     */
    private var registerResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Disposable to dispose of requests
     */
    private var disposable = CompositeDisposable()

    init {
        // Inject Services with Dagger
        App.appComponent.inject(this)
    }

    private fun onRegisterStart() {
        // Set the loading status to visible
        loadingVisibility.value = true
    }

    private fun onRegisterFinish() {
        // Set the loading status to not visible
        loadingVisibility.value = false
    }

    private fun onRegisterSuccess() {
        // Set the result to a Success status
        this.registerResult.value = Resource(Status.SUCCESS, null, null)
    }

    private fun onRegisterError(e: Throwable) {
        // Set the result to an Error status
        if (e is UnAuthorizedException) this.registerResult.value = Resource(Status.ERROR, null, "Incorrect Login Credentials")
        else this.registerResult.value = Resource(Status.ERROR, null, "Bad request")
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getRegisterResult(): LiveData<Resource<Boolean>> {
        return registerResult
    }

    /**
     * Register a new user.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    fun register(username: String, password: String) {
        disposable.clear()
        disposable.add(userService.register(username, password)
            .doOnSubscribe { onRegisterStart() }
            .subscribe(
                { res -> onRegisterSuccess(); onRegisterFinish() },
                { error -> onRegisterError(error); onRegisterFinish() }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose of the request
        disposable.dispose()
    }

}