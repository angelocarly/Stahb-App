package be.magnias.stahb.network

import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor : Interceptor {

    var token : String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZDI0OWQyYmFlMzEwNTA2ZjhjYWQ2YjgiLCJ1c2VybmFtZSI6ImFuZ2VsbyIsImV4cCI6MTU2Nzg2NTc4OSwiaWF0IjoxNTYyNjgxNzg5fQ._S61OCLlN72X5KCmxKd3Y1fZmEMFTsdRfAUH3NQ06C4"

    fun Token(token: String ) {
        this.token = token;
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.header("No-Authentication")==null){
            //val token = getTokenFromSharedPreference();
            //or use Token Function
            if(!token.isNullOrEmpty())
            {
                val finalToken =  "Bearer "+token
                request = request.newBuilder()
                    .addHeader("Authorization",finalToken)
                    .build()
            }

        }

        return chain.proceed(request)
    }

}