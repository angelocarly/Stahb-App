package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.dao.TabDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabRepository(private val tabDao: TabDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun fetch(): Observable<Boolean> {

        //Refresh the data in the cache
        //Fetches the favorites and new tabs from the api, update the cache when both are loaded
        return Observable
            .combineLatest(
                loadFavoritesFromApi().singleOrError().toObservable().subscribeOn(Schedulers.io()),
                loadTabsFromApi().singleOrError().toObservable().subscribeOn(Schedulers.io()),
                BiFunction { favorites: List<Tab>, tabs : List<Tab> ->
                    tabDao.updateAll(favorites, tabs)
                }
            )
            .map { true }
    }

    fun getTab(id: String): Observable<Tab> {
        return Observable.concat(
            loadTabFromCache(id).filter { t -> t.loaded },
            loadTabFromApi(id)
        )
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    private fun loadTabFromApi(id: String): Observable<Tab> {
        //Load tab from network
        //Tries to insert the loaded tab, without overwriting
        //Next, it updates the fields of the database field incase the write failed
        //TODO: Simplify function
        return stahbApi.getTab(id)
            .doOnNext {
                it.loaded = true
                tabDao.insertIgnoreConflict(it)
                tabDao.updateFields(
                    it._id,
                    it.artist,
                    it.song,
                    it.tab!!,
                    it.tuning!!,
                    true
                )
                Logger.d("Dispatching tab $id from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabFromCache(id: String): Observable<Tab> {
        return tabDao.getTab(id)
            .toObservable()
            .doOnNext {
                Logger.d("Dispatching tab $id from database")
            }
            .subscribeOn(Schedulers.io())
    }

    fun getAllTabs(): Observable<List<Tab>> {

        return loadTabsFromCache()
    }

    fun getFavoriteTabs(): Observable<List<Tab>> {
        return loadFavoritesFromCache()
    }

    private fun loadFavoritesFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getFavorites()
    }

    private fun loadFavoritesFromCache(): Observable<List<Tab>> {
        return tabDao.getFavorites()
    }

    private fun loadTabsFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getAllTabInfo()
    }

    private fun loadTabsFromCache(): Observable<List<Tab>> {
        return tabDao.getAllTabs()
    }

}