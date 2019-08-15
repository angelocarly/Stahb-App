package be.magnias.stahb.service

import io.reactivex.Single
import io.reactivex.subjects.Subject

interface IUserService {

    fun isUserLoggedIn(): Boolean

    fun getUserLoggedIn(): Subject<Boolean>

    /**
     * Log the user out
     */
    fun logout(): Single<Boolean>

    /**
     * Log the user in
     * @param username The user's username
     * @param password The user's password
     * @return A Single that return true on success
     * @throws UnAuthorizedException on invalid credentials
     * @throws BadRequestException on failed requests
     */
    fun login(username: String, password: String): Single<Boolean>

    /**
     * Register a user.
     * @param username The user's username.
     * @param password The user's password.
     * @return A Single that return true on success
     * @throws BadRequestException on failure
     */
    fun register(username: String, password: String): Single<Boolean>

}