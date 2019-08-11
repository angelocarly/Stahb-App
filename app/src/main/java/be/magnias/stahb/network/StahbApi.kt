package be.magnias.stahb.network

import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.Token
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 * The interface which provides methods to get result of webservices
 */
interface StahbApi {

    /**
     * Get the list of all the tabs from the API
     */
    @GET("/tabs")
    fun getAllTabInfo(): Observable<List<Tab>>

    /**
     * Get a tab by its id
     */
    @GET("/tabs/{id}")
    fun getTab(@Path("id") id: String): Observable<Tab>

    /**
     * Get the list of all the favorite tabs from the user
     */
    @GET("/user/fav")
    fun getFavorites(): Observable<List<Tab>>

    /**
     * Add a tab by its id to the user's favorites
     */
    @POST("/user/fav/{id}")
    fun addFavorite(@Path("id") id: String): Call<Void>

    /**
     * Remove a tab by its id from the user's favorites
     */
    @DELETE("user/fav/{id}")
    fun deleteFavorite(@Path("id") id: String): Call<Void>

    /**
     * Request an access token by posting login info
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String): Call<Token>

    /**
     * Register a user account by posting login details
     */
    @POST("/user/register")
    @FormUrlEncoded
    fun register(@Field("username") username: String, @Field("password") password: String): Call<Token>
}