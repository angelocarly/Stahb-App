package be.magnias.stahb.network

import be.magnias.stahb.App
import be.magnias.stahb.persistence.UserRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ServiceInterceptor : Interceptor {

    @Inject
    lateinit var userRepository: UserRepository

    init {
        App.appComponent.inject(this)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.header("No-Authentication")==null){
            //val token = getTokenFromSharedPreference();
            //or use Token Function
            val token = userRepository.getUserToken()
            if(!token.token.isNullOrEmpty())
            {
                val finalToken =  "Bearer "+token.token
                request = request.newBuilder()
                    .addHeader("Authorization",finalToken)
                    .build()
            }

        }

        return chain.proceed(request)
    }

}