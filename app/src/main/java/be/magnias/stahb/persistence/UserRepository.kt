package be.magnias.stahb.persistence

import android.content.Context
import be.magnias.stahb.App
import be.magnias.stahb.model.Token
import com.orhanobut.logger.Logger
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Inject

class UserRepository {

    private lateinit var userToken: Token

    @Inject
    lateinit var context: Context

    init {
        App.appComponent.inject(this)

        this.userToken = Token(context.defaultSharedPreferences.getString("userToken", null))
    }

    fun saveUserToken(userToken: Token?) {
        if (userToken == null) {
            this.userToken = Token(null)
            context.defaultSharedPreferences.edit().putString("userToken", null).apply()
        } else {
            this.userToken = userToken
            context.defaultSharedPreferences.edit().putString("userToken", userToken?.token).apply()
        }
    }

    fun getUserToken(): Token {
        return userToken
    }

    fun isUserLoggedIn(): Boolean {
        return getUserToken().token != null
    }

}