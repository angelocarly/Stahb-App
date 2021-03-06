package be.magnias.stahb.persistence

import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.dao.TabDao
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync

/**
 * Class that manages the access of tabs in the cache.
 */
class TabRepository(private val tabDao: TabDao) : ITabRepository {

    /**
     * Load a tab from the cache.
     * Returns nothing when no tab is found.
     */
    override fun loadTab(id: String): Observable<Tab> {
        return tabDao.getTab(id)
            .doOnNext {
                Logger.d("Dispatching tab $id from database")
            }
            .subscribeOn(Schedulers.io())
    }

    /**
     * @return A list of ALL the stored tabs in cache.
     */
    override fun loadTabs(): Observable<List<Tab>> {
        return tabDao.getAllTabs()
    }

    /**
     * @return A list of all the favorite tabs stored in cache.
     */
    override fun loadFavorites(): Observable<List<Tab>> {
        return tabDao.getFavorites()
    }

    /**
     * @return A list of all the tabs that were updated by the user since the last refresh.
     */
    override fun getAllEditedTabs(): Observable<List<Tab>> {
        return tabDao.getAllUpdatedTabs()
    }

    /**
     * Add a tab to the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    override fun addFavorite(id: String) {
        doAsync {
            tabDao.addFavorite(id)
        }
    }

    /**
     * Store a tab in cache.
     * @param tab The tab you want to save
     */
    override fun storeTab(tab: Tab) {
        tab.loaded = true
        // Insert ignore on conflict to not overwrite favorite status
        val inserted = tabDao.insertIgnoreOnConflict(tab)
        if (inserted == -1L) {
            tabDao.updateFields(
                tab._id,
                tab.artist,
                tab.song,
                tab.tab!!,
                tab.tuning!!,
                true
            )
        }
    }

    /**
     * Mark all the stored tabs as fresh.
     */
    override fun setAllTabsFresh() {
        tabDao.setAllTabsFresh()
    }

    /**
     * Update all the stored tabs, removes any tabs not contained in the list and adds the new ones.
     */
    override fun updateAll(favorites: List<Tab>, tabs: List<Tab>) {
        tabDao.updateAll(favorites, tabs)
    }

    /**
     * Update all the stored tabs, removes any tabs not contained in the list and adds the new ones.
     */
    override fun updateAll(tabs: List<Tab>) {
        tabDao.updateAll(tabs)
    }

    /**
     * Remove a tab from the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    override fun removeFromFavorites(id: String) {
        doAsync {
            tabDao.removeFavorite(id)
        }
    }
}