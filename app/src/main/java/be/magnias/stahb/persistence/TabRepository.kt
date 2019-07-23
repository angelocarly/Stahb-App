package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.dao.TabDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
        //TODO: change onerror ignoring to resource object
        return Observable
            .combineLatest(
                loadFavoritesFromApi().singleOrError().toObservable().subscribeOn(Schedulers.io()),
                loadTabsFromApi().singleOrError().toObservable().onErrorResumeNext(Observable.empty()).subscribeOn(
                    Schedulers.io()
                ),
                BiFunction { favorites: List<Tab>, tabs: List<Tab> ->
                    tabDao.updateAll(favorites, tabs)
                }
            )
            .map { true }
    }

    //Load tab from network
    //Tries to insert the loaded tab, without overwriting
    //Next, it updates the fields of the database field incase the write failed
    //TODO: Simplify function
    fun fetchTab(id: String): Observable<Tab> {
        return loadTabFromApi(id)
            .doOnNext {
                it.loaded = true
                val inserted = tabDao.insertIgnoreConflict(it)
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

    fun getTab(id: String): Observable<Tab> {
        return loadTabFromCache(id)
            .doOnNext { if(!it.loaded) fetchTab(id).subscribe() }
            .filter { t -> t.loaded }
    }

    private fun loadTabFromApi(id: String): Observable<Tab> {
        return stahbApi.getTab(id)
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabFromCache(id: String): Observable<Tab> {
        return tabDao.getTab(id)
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

    fun addFavorite(id: String): Disposable {
        return Single.just(tabDao)
            .subscribeOn(Schedulers.io())
            .subscribe { tabDao -> tabDao.addFavorite(id) }
    }

    fun removeFromFavorites(id: String): Disposable {
        return Single.just(tabDao)
            .subscribeOn(Schedulers.io())
            .subscribe { tabDao -> tabDao.removeFavorite(id) }
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