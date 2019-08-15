package be.magnias.stahb.persistence

import android.content.Context
import be.magnias.stahb.App
import be.magnias.stahb.model.Token
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Inject

/**
 * Repository for User data.
 */
class UserRepository : IUserRepository{

    /**
     * In memory cache of the user's token.
     */
    private var userToken: Token

    @Inject
    lateinit var context: Context

    init {
        // Inject context with Dagger.
        App.appComponent.inject(this)

        // Load the saved token or null in memory when the app starts.
        this.userToken = Token(context.defaultSharedPreferences.getString("userToken", null))
    }

    /**
     * Persist a user token.
     * @param userToken The userToken or null
     */
    override fun saveUserToken(userToken: Token) {
        // Store the token
        this.userToken = userToken
        context.defaultSharedPreferences.edit().putString("userToken", userToken.token).apply()

    }

    /**
     * Remove a user token.
     */
    override fun removeUserToken() {
        // Clear the token in memory and shared preferences.
        this.userToken = Token(null)
        context.defaultSharedPreferences.edit().putString("userToken", null).apply()
    }

    override fun getUserToken(): Token {
        return userToken
    }

    override fun isUserLoggedIn(): Boolean {
        return getUserToken().token != null
    }

}