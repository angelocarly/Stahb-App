package be.magnias.stahb.network

import be.magnias.stahb.model.Tab
import io.reactivex.Observable
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
    fun getTabs(): Observable<List<Tab>>

    @GET("/tabs/{id}")
    fun getTab(@Path("id") id: String): Observable<Tab>
}