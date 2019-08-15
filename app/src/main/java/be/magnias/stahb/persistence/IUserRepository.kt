package be.magnias.stahb.persistence

import be.magnias.stahb.model.Token
import org.jetbrains.anko.defaultSharedPreferences

interface IUserRepository {

    /**
     * Persist a user token.
     * @param userToken The userToken or null
     */
    fun saveUserToken(userToken: Token)

    /**
     * Remove a user token.
     */
    fun removeUserToken()

    fun getUserToken(): Token

    fun isUserLoggedIn(): Boolean

}