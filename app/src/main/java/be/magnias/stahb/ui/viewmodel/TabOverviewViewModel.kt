package be.magnias.stahb.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.magnias.stahb.App
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.persistence.UserService
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TabOverviewViewModel : ViewModel() {

    @Inject
    lateinit var userService: UserService

    private var isUserLoggedIn = MutableLiveData<Boolean>()

    private var subscription: Disposable

    init {
        App.appComponent.inject(this)

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
        super.onCleared()
    }
}