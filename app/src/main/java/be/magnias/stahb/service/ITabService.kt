package be.magnias.stahb.service

import be.magnias.stahb.model.Tab
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface ITabService {

    /**
     * Send any new changes to the backend.
     * @return Observable returning true on success.
     */
    fun push(): Single<Boolean>

    /**
     * Get the latest version of the tabs from the backend.
     */
    fun fetch(): Single<Boolean>

    /**
     * Perform both a push and a fetch.
     */
    fun refresh(): Flowable<Boolean>

    /**
     * Public call to request a tab.
     * If the tab in the database is not loaded entirely, then request the full tab from the backend.
     */
    fun getTab(id: String): Observable<Tab>

    fun getAllTabs(): Observable<List<Tab>>

    fun getFavoriteTabs(): Observable<List<Tab>>

    /**
     * Remove a tab from the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun removeFromFavorites(id: String)

    /**
     * Add a tab to the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun addFavorite(id: String)

}