package be.magnias.stahb.service

import be.magnias.stahb.App
import be.magnias.stahb.error.BadRequestException
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Token
import be.magnias.stahb.network.StahbApi
import be.magnias.stahb.persistence.UserRepository
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

/**
 * Service to manage the user.
 */
class UserService {

    @Inject
    lateinit var stahbApi: StahbApi

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var tabService: TabService

    /**
     * Subject to notify any listeners for changes in the user state.
     */
    private var loggedInSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    private var loginDisposable = CompositeDisposable()

    init {
        // Inject services using Dagger
        App.appComponent.inject(this)

        // Update the loggedInSubject with an initial value.
        // This way subscribers immediately have data at startup.
        loggedInSubject.onNext(isUserLoggedIn())
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.getUserToken().token != null
    }

    fun getUserLoggedIn(): Subject<Boolean> {
        return loggedInSubject
    }

    /**
     * Log the user out
     */
    fun logout(): Single<Boolean> {
        return saveLogin(null)
    }

    /**
     * Log the user in
     * @param username The user's username
     * @param password The user's password
     * @return A Single that return true on success
     * @throws UnAuthorizedException on invalid credentials
     * @throws BadRequestException on failed requests
     */
    fun login(username: String, password: String): Single<Boolean> {

        return Single.create<Boolean> { s ->
            // Execute the call
            val res = stahbApi.login(username, password).execute()

            if (!res.isSuccessful) {
                when (res.code()) {
                    401 -> {
                        Logger.d("Failed logging in, unauthorized")
                        s.onError(UnAuthorizedException())
                    }
                    else -> {
                        Logger.d("Failed logging in")
                        s.onError(BadRequestException())
                    }
                }
            } else {
                // Persist our token
                saveLogin(res.body()).subscribe(
                    { s.onSuccess(true)},
                    { s.onError(it)}
                )
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Register a user.
     * @param username The user's username.
     * @param password The user's password.
     * @return A Single that return true on success
     * @throws BadRequestException on failure
     */
    fun register(username: String, password: String): Single<Boolean> {
        return Single.create<Boolean> {
            val res = stahbApi.register(username, password).execute()

            if (!res.isSuccessful) {
                Logger.d("Failed registering")
                it.onError(BadRequestException())
            } else {
                // Return a value to mark success
                it.onSuccess(true)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Persist a token in the repository.
     * Transmits any unsaved favorites to the backend first.
     * When the login info is saved, fetch all the favorites.
     */
    private fun saveLogin(userToken: Token?): Single<Boolean> {

        loginDisposable.clear()
        return Single.create<Boolean> { s ->
            // If another user is still logged in, push his updates first
            if (isUserLoggedIn()) {
                loginDisposable.add(
                    tabService.push()
                        .subscribe({}, { error ->
                            s.tryOnError(error)
                        })
                )

                // Save the token
                if (userToken == null) userRepository.removeUserToken()
                else userRepository.saveUserToken(userToken)

                // Update favorites list
                loginDisposable.add(
                    tabService.fetch()
                        .subscribe({}, { error ->
                            s.tryOnError(error)
                        })
                )
            }

            // Save the token
            if (userToken == null) userRepository.removeUserToken()
            else userRepository.saveUserToken(userToken)

            // Refresh favorites list
            loginDisposable.add(
                tabService.fetch()
                    .subscribe({}, { error ->
                        s.tryOnError(error)
                    })
            )

            // Push the logged in status to our subject
            loggedInSubject.onNext(userToken != null)

            s.onSuccess(true)
        }.observeOn(AndroidSchedulers.mainThread())
    }

}