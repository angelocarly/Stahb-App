package be.magnias.stahb.service

import be.magnias.stahb.App
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Tab
import be.magnias.stahb.persistence.dao.TabDao
import be.magnias.stahb.network.StahbApi
import be.magnias.stahb.persistence.UserRepository
import com.orhanobut.logger.Logger
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import javax.inject.Inject

/**
 * Service for User data.
 * Handles caching with the Database as its single source of truth.
 */
class TabService(private val tabDao: TabDao) {

    @Inject
    lateinit var stahbApi: StahbApi

    @Inject
    lateinit var userRepository: UserRepository

    init {
        // Inject Services with Dagger
        App.appComponent.inject(this)
    }

    /**
     * Send any new changes to the backend.
     * @return Observable returning true on success.
     */
    fun push(): Single<Boolean> {

        // If no user is logged in then we can't push any data
        if (!userRepository.isUserLoggedIn()) {
            return Single.create { it.onSuccess(false) }
        }


        var tabs: List<Tab>? = null
        return tabDao
            // Retrieve all the changed tabs in the database
            .getAllUpdatedTabs().firstOrError()
            .doOnSuccess {
                // Store them in a temporary variable
                tabs = it
            }
            .doOnSuccess {
                // Send each tab to the backend
                // TODO streamline the operation to use less network calls.
                tabs?.forEach { t ->
                    if (t.favorite) {
                        stahbApi.addFavorite(t._id).execute()
                    } else {
                        stahbApi.deleteFavorite(t._id).execute()
                    }
                }
                tabDao.setAllTabsFresh()
            }
            .map { true }
    }

    /**
     * Get the latest version of the tabs from the backend.
     */
    fun fetch(): Single<Boolean> {
        //Refresh the data in the cache
        if (userRepository.isUserLoggedIn()) {
            //Fetches the favorites and new tabs from the api, update the cache when both are loaded
            return Single.zip(
                loadFavoritesFromApi()
                    .subscribeOn(Schedulers.io()),
                loadTabsFromApi()
                    .subscribeOn(Schedulers.io()),
                BiFunction { favorites: List<Tab>, tabs: List<Tab> ->
                    tabDao.updateAll(favorites, tabs)
                }
            )
                .map { true }
        } else {
            // Only load the new list when user is not logged in
            return loadTabsFromApi()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    tabDao.updateAll(it)
                }
                .map { true }
        }
    }

    /**
     * Perform both a push and a fetch.
     */
    fun refresh(): Flowable<Boolean> {
        return Single.concat(
            push(),
            fetch()
        )
    }

    /**
     * Public call to request a tab.
     * If the tab in the database is not loaded entirely, then request the full tab from the backend.
     */
    fun getTab(id: String): Observable<Tab> {
        return loadTabFromCache(id)
            .concatMap {
                // If the tab is not cached, load it from the api
                if (!it.loaded) loadTabFromApi(id).toObservable()
                // Else all is fine and use the cache
                else Observable.just(it)
            }

    }

    /**
     * Load a tab from the backend.
     * Stores the tab in the cache when loaded.
     * @param id The id of the requested tab.
     * @return Observable with the requested tab.
     */
    private fun loadTabFromApi(id: String): Single<Tab> {
        return stahbApi.getTab(id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                it.loaded = true
                // Insert ignore on conflict to not overwrite favorite status
                val inserted = tabDao.insertIgnoreOnConflict(it)
                if (inserted == -1L) {
                    tabDao.updateFields(
                        it._id,
                        it.artist,
                        it.song,
                        it.tab!!,
                        it.tuning!!,
                        true
                    )
                }
                Logger.d("Dispatching tab $id from API")
            }
    }

    /**
     * Load a tab from the cache.
     * Returns nothing when no tab is found.
     */
    private fun loadTabFromCache(id: String): Observable<Tab> {
        return tabDao.getTab(id)
            .doOnNext {
                Logger.d("Dispatching tab $id from database")
            }
            .subscribeOn(Schedulers.io())
    }

    /**
     * Get all the stored tabs in the database.
     * TODO refresh data when it's too stale.
     */
    fun getAllTabs(): Observable<List<Tab>> {
        return loadTabsFromCache()
    }

    private fun loadTabsFromApi(): Single<List<Tab>> {
        // Load tabs from network
        return stahbApi.getAllTabInfo()
    }

    private fun loadTabsFromCache(): Observable<List<Tab>> {
        // Load tabs from cache
        return tabDao.getAllTabs()
    }

    /**
     * Get all the stored favorite tabs in the database.
     * TODO refresh data when it's too stale.
     */
    fun getFavoriteTabs(): Observable<List<Tab>> {
        return loadFavoritesFromCache()
    }

    private fun loadFavoritesFromApi(): Single<List<Tab>> {
        // Load favorites from network
        if (!userRepository.isUserLoggedIn()) {
            return Single.create {
                it.tryOnError(UnAuthorizedException())
            }
        }

        return stahbApi.getFavorites()
    }

    private fun loadFavoritesFromCache(): Observable<List<Tab>> {
        return tabDao.getFavorites()
    }

    /**
     * Add a tab to the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun addFavorite(id: String) {
        doAsync {
            tabDao.addFavorite(id)
        }
    }

    /**
     * Remove a tab from the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    fun removeFromFavorites(id: String) {
        doAsync {
            tabDao.removeFavorite(id)
        }
    }

}