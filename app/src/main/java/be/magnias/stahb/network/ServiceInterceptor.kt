package be.magnias.stahb.network

import be.magnias.stahb.App
import be.magnias.stahb.persistence.IUserRepository
import be.magnias.stahb.persistence.UserRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Authorization interceptor.
 * Adds Authorization Header to any outbound request.
 */
class ServiceInterceptor : Interceptor {

    @Inject
    lateinit var userRepository: IUserRepository

    init {
        // Inject services with Dagger
        App.appComponent.inject(this)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {

            val token = userRepository.getUserToken()
            if (!token.token.isNullOrEmpty()) {
                val finalToken = "Bearer " + token.token
                request = request.newBuilder()
                    .addHeader("Authorization", finalToken)
                    .build()
            }

        }

        return chain.proceed(request)
    }

}