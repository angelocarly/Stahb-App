package be.magnias.stahb.persistence

import be.magnias.stahb.model.Tab
import io.reactivex.Observable

interface ITabRepository {

    /**
     * Load a tab from the cache.
     * Returns nothing when no tab is found.
     */
    fun loadTab(id: String): Observable<Tab>
    /**
     * @return A list of ALL the stored tabs in cache.
     */
    fun loadTabs(): Observable<List<Tab>>

    /**
     * @return A list of all the favorite tabs stored in cache.
     */
    fun loadFavorites(): Observable<List<Tab>>

    /**
     * @return A list of all the tabs that were updated by the user since the last refresh.
     */
    fun getAllEditedTabs(): Observable<List<Tab>>

    /**
     * Add a tab to the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun addFavorite(id: String)

    /**
     * Store a tab.
     * @param tab The tab you want to save
     */
    fun storeTab(tab: Tab)

    /**
     * Mark all the stored tabs as fresh.
     */
    fun setAllTabsFresh()

    /**
     * Update all the stored tabs, removes any tabs not contained in the list and adds the new ones.
     */
    fun updateAll(favorites: List<Tab>, tabs: List<Tab>)

    /**
     * Update all the stored tabs, removes any tabs not contained in the list and adds the new ones.
     */
    fun updateAll(tabs: List<Tab>)

    /**
     * Remove a tab from the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun removeFromFavorites(id: String)

}