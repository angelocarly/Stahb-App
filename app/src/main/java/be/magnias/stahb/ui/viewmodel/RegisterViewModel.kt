package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.persistence.UserService
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RegisterViewModel : ViewModel() {

    @Inject
    lateinit var userService: UserService

    private var registerResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    /**
     * Indicates whether the loading view should be displayed.
     */
    private val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    private var disposable = CompositeDisposable()

    init {
        App.appComponent.inject(this)
    }

    private fun onRegisterStart() {
        loadingVisibility.value = true
    }

    private fun onRegisterFinish() {
        loadingVisibility.value = false
    }

    private fun onRegisterSuccess() {
        this.registerResult.value = Resource(Status.SUCCESS, null, null)
    }

    private fun onRegisterError(e: Throwable) {
        if (e is UnAuthorizedException) this.registerResult.value = Resource(Status.ERROR, null, "Incorrect Login Credentials")
        else this.registerResult.value = Resource(Status.ERROR, null, "Bad request")
    }

    fun getLoadingVisibility(): LiveData<Boolean> {
        return loadingVisibility
    }

    fun getRegisterResult(): LiveData<Resource<Boolean>> {
        return registerResult
    }

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
        disposable.dispose()
    }

}