package be.magnias.stahb.network

import be.magnias.stahb.model.Tab
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

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

    @POST("/user/fav/{id}")
    fun addFavorite(@Path("id") id: String): Call<Void>

    @DELETE("user/fav/{id}")
    fun deleteFavorite(@Path("id") id: String): Call<Void>
}