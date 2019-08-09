package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.persistence.UserService
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginViewModel : ViewModel() {

    @Inject
    lateinit var userService: UserService

    private var loginResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    private var disposable = CompositeDisposable()

    init {
        App.appComponent.inject(this)
    }

    private fun onLoginStart() {
        loadingVisibility.value = true
    }

    private fun onLoginFinish() {
        Logger.d("FINIIIIISH")
        loadingVisibility.value = false
    }

    private fun onLoginSuccess() {
        this.loginResult.value = Resource(Status.SUCCESS, null, null)
    }

    private fun onLoginError(e: Throwable) {
        if (e is UnAuthorizedException) this.loginResult.value = Resource(Status.ERROR, null, "Incorrect Login Credentials")
        else this.loginResult.value = Resource(Status.ERROR, null, "Bad request")
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getLoginResult(): LiveData<Resource<Boolean>> {
        return loginResult
    }

    fun login(username: String, password: String) {
        disposable.clear()
        disposable.add(userService.login(username, password)
            .doOnSubscribe { onLoginStart() }
            .subscribe(
                { res -> onLoginSuccess(); onLoginFinish() },
                { error -> onLoginError(error); onLoginFinish() }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}