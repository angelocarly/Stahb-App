package be.magnias.stahb.persistence

import android.os.AsyncTask
import android.util.Log
import androidx.annotation.WorkerThread
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
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TabRepository(private val tabDao: TabDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }


    fun push(): Observable<Boolean> {
        var tabs : List<Tab>? = null
        return tabDao.getAllUpdatedTabs().firstOrError()
            .doOnSuccess {
                tabs = it
            }
            .map { true }
            .toObservable()
            .doOnComplete {
                tabs?.forEach { t ->
                    if (t.favorite) {
                        stahbApi.addFavorite(t._id).execute()
                    }
                    else {
                        stahbApi.deleteFavorite(t._id).execute()
                    }
                }
                Logger.w("Updated push")
            }
    }

    fun fetch(): Observable<Boolean> {
        //Refresh the data in the cache
        //Fetches the favorites and new tabs from the api, update the cache when both are loaded
        return Observable.combineLatest(
            loadFavoritesFromApi()
                .singleOrError()
                .toObservable()
                .subscribeOn(Schedulers.io()),
            loadTabsFromApi()
                .singleOrError()
                .toObservable()
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.io()),
            BiFunction { favorites: List<Tab>, tabs: List<Tab> ->
                tabDao.updateAll(favorites, tabs)
                Logger.w("Updated fetch")
            }
        )
            .map { true }
    }

    fun refresh(): Observable<Boolean> {
        return Observable.concat(
            push(),
            fetch()
        )
    }

    //Load tab from network
//Tries to insert the loaded tab, without overwriting
//Next, it updates the fields of the database field incase the write failed
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
            .doOnNext { if (!it.loaded) fetchTab(id).subscribe() }
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

    fun addFavorite(id: String) {
        doAsync {
            tabDao.addFavorite(id)
        }
    }

    fun removeFromFavorites(id: String) {
        doAsync {
            tabDao.removeFavorite(id)
        }
    }

    private fun addToFavoritesApi(id: String) {
        stahbApi.addFavorite(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Logger.d("[TabRepository] Successfully added favorite on server")
                } else {
                    Logger.d("[TabRepository] Failed adding favorite on server")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Logger.d("[TabRepository] Failed adding favorite on server")
            }
        })

    }

    private fun removeFromFavoritesApi(id: String) {
        stahbApi.deleteFavorite(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Logger.d("[TabRepository] Successfully removed favorite on server")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Logger.d("[TabRepository] Failed removing favorite on server")
            }
        })
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