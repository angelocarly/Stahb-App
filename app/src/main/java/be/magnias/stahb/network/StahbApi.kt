package be.magnias.stahb.network

import be.magnias.stahb.model.Tab
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The interface which provides methods to get result of webservices
 */
interface StahbApi {
    /**
     * Get the list of the tabs from the API
     */
    @GET("/tabs")
    fun getAllTabInfo(): Observable<List<Tab>>

    @GET("/tabs/{id}")
    fun getTab(@Path("id") id: String): Observable<Tab>

    @GET("/user/fav")
    fun getFavorites(): Observable<List<Tab>>
}