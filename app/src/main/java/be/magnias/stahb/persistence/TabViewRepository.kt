package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabView
import be.magnias.stahb.model.TabViewTab
import be.magnias.stahb.model.dao.TabDao
import be.magnias.stahb.model.dao.TabViewDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabViewRepository(private val tabDao: TabDao, private val tabViewDao: TabViewDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun getAllTabs(): Observable<List<Tab>> {
        return Observable.concat(
            loadTabsFromCache(),
            loadTabsFromApi())
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    fun getFavoriteTabs(): Observable<List<Tab>> {
        return Observable.concat(
            loadFavoritesFromCache(),
            loadFavoritesFromApi())
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    private fun loadFavoritesFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getFavorites()
            .doOnNext {
                tabViewDao.removeTabViewTabsByViewId(FAVORITES_ID)
                tabDao.insertAll(it)
                tabViewDao.linkTabs(it.map { t -> TabViewTab(FAVORITES_ID, t._id) })
                Logger.d("Dispatching favorites from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadFavoritesFromCache(): Observable<List<Tab>> {
        return tabViewDao.getTabViewTabs(FAVORITES_ID)
            .toObservable().filter { it.isNotEmpty() }
            .doOnNext {
                Logger.d("Dispatching tabs from database")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabsFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getAllTabInfo()
            .doOnNext {
                tabViewDao.removeTabViewTabsByViewId(NEW_ID)
                tabDao.insertAll(it)
                tabViewDao.linkTabs(it.map { t -> TabViewTab(NEW_ID, t._id) })

                Logger.d("Dispatching tabs from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabsFromCache(): Observable<List<Tab>> {
        return tabViewDao.getTabViewTabs(NEW_ID)
            .toObservable().filter { it.isNotEmpty() }
            .doOnNext {
                Logger.d("Dispatching tabs from database")
            }
            .subscribeOn(Schedulers.io())
    }


}