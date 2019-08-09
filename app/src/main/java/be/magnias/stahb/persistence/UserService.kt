package be.magnias.stahb.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.magnias.stahb.App
import be.magnias.stahb.error.BadRequestException
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Token
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserService {

    @Inject
    lateinit var stahbApi: StahbApi

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var tabRepository: TabRepository

    private var loggedInSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    init {
        App.appComponent.inject(this)

        loggedInSubject.onNext(isUserLoggedIn())
     }

    //Get token
    fun getUserToken(): Token? {
        return userRepository.getUserToken()
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.getUserToken().token != null
    }

    fun getUserLoggedIn(): Subject<Boolean> {
        return loggedInSubject
    }

    //Logout user
    fun logout() {
        saveLogin(null)
    }

    //Login user
    fun login(username: String, password: String): Single<Boolean> {

        return Observable.create<Boolean> {
            val res = stahbApi.login(username,password).execute()
            if (!res.isSuccessful) {
                when (res.code()) {
                    401 -> {
                        Logger.d("Failed logging in, unauthorized")
                        throw UnAuthorizedException()
                    }
                    else -> {
                        Logger.d("Failed logging in")
                        throw BadRequestException()
                    }
                }
            } else {
                saveLogin(res.body())
                //Return a value to mark success
                it.onNext(true)
            }
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .singleOrError()
    }

    //Save a new token in the repository
    private fun saveLogin(userToken: Token?) {
        //If another user is still logged in, push his updates first
        if(isUserLoggedIn()) {
            tabRepository.push()
                .doOnComplete {
                    //Save the token
                    userRepository.saveUserToken(userToken)
                    //Update favorites list
                    tabRepository.fetch().subscribe()
                }
                .subscribe()
        } else {
            //Save the token
            userRepository.saveUserToken(userToken)
            //Update favorites list
            tabRepository.fetch().subscribe()
        }

        //Push the logged in status to our subject
        loggedInSubject.onNext(userToken != null)
    }

}