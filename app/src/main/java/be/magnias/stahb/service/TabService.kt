package be.magnias.stahb.service

import be.magnias.stahb.App
import be.magnias.stahb.error.UnAuthorizedException
import be.magnias.stahb.model.Tab
import be.magnias.stahb.network.StahbApi
import be.magnias.stahb.persistence.ITabRepository
import be.magnias.stahb.persistence.IUserRepository
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.persistence.UserRepository
import com.orhanobut.logger.Logger
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Service for User data.
 * Handles caching with the Database as its single source of truth.
 */
class TabService : ITabService {

    @Inject
    lateinit var stahbApi: StahbApi

    @Inject
    lateinit var userRepository: IUserRepository

    @Inject
    lateinit var tabRepository: ITabRepository

    init {
        // Inject Services with Dagger
        App.appComponent.inject(this)
    }

    /**
     * Send any new changes to the backend.
     * @return Observable returning true on success.
     */
    override fun push(): Single<Boolean> {

        // If no user is logged in then we can't push any data
        if (!userRepository.isUserLoggedIn()) {
            return Single.create { it.onSuccess(false) }
        }


        return tabRepository
            // Retrieve all the changed tabs in the database
            .getAllEditedTabs().firstOrError()
            .doOnSuccess {tabs ->
                // Send each tab to the backend
                // TODO streamline the operation to use less network calls.
                tabs?.forEach { t ->
                    if (t.favorite) {
                        stahbApi.addFavorite(t._id).execute()
                    } else {
                        stahbApi.deleteFavorite(t._id).execute()
                    }
                }
                tabRepository.setAllTabsFresh()
            }
            .map { true }
    }

    /**
     * Get the latest version of the tabs from the backend.
     */
    override fun fetch(): Single<Boolean> {
        //Refresh the data in the cache
        if (userRepository.isUserLoggedIn()) {
            //Fetches the favorites and new tabs from the api, update the cache when both are loaded
            return Single.zip(
                loadFavoritesFromApi()
                    .subscribeOn(Schedulers.io()),
                loadTabsFromApi()
                    .subscribeOn(Schedulers.io()),
                BiFunction { favorites: List<Tab>, tabs: List<Tab> ->
                    tabRepository.updateAll(favorites, tabs)
                }
            )
                .map { true }
        } else {
            // Only load the new list when user is not logged in
            return loadTabsFromApi()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    tabRepository.updateAll(it)
                }
                .map { true }
        }
    }

    /**
     * Perform both a push and a fetch.
     */
    override fun refresh(): Flowable<Boolean> {
        return Single.concat(
            push(),
            fetch()
        )
    }

    /**
     * Public call to request a tab.
     * If the tab in the database is not loaded entirely, then request the full tab from the backend.
     */
    override fun getTab(id: String): Observable<Tab> {
        return tabRepository.loadTab(id)
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
                tabRepository.storeTab(it)
                Logger.d("Dispatching tab $id from API")
            }
    }

    /**
     * Get all the stored tabs in the database.
     * TODO refresh data when it's too stale.
     */
    override fun getAllTabs(): Observable<List<Tab>> {
        return tabRepository.loadTabs()
    }

    private fun loadTabsFromApi(): Single<List<Tab>> {
        // Load tabs from network
        return stahbApi.getAllTabInfo()
    }


    /**
     * Get all the stored favorite tabs in the database.
     * TODO refresh data when it's too stale.
     */
    override fun getFavoriteTabs(): Observable<List<Tab>> {
        return tabRepository.loadFavorites()
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

    /**
     * Remove a tab from the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    override fun removeFromFavorites(id: String) {
        tabRepository.removeFromFavorites(id)
    }

    /**
     * Add a tab to the favorites.
     * Changes are not posted immediately.
     * Do a push() or refresh() to update data on the backend.
     */
    override fun addFavorite(id: String) {
        tabRepository.addFavorite(id)
    }

}