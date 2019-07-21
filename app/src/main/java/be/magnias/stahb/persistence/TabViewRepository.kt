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
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class TabViewRepository(private val tabDao: TabDao, private val tabViewDao: TabViewDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun getAllTabs(): Observable<List<Tab>> {

        return Observable.concatArray(
            loadTabsFromCache(),
            loadTabsFromApi()
        )
//        return Observable.concatArray(
//            loadTabsFromCache()
//                .doOnNext { t: List<Tab>? ->
//                    Logger.d("update")
//                },
//            loadTabsFromApi())
////            .firstElement()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFavoriteTabs(): Observable<List<Tab>> {
        return Observable.concatArray(
            loadFavoritesFromCache(),
            loadFavoritesFromApi()
        )
    }

    fun refreshAllTabs(): Observable<List<Tab>> {
        return loadTabsFromApi()
            .doOnError { e -> Logger.e(e.message.toString()) }
    }

    fun refreshFavoriteTabs(): Observable<List<Tab>> {
        return loadFavoritesFromApi()
            .doOnError { e -> Logger.e(e.message.toString()) }
    }

    private fun loadFavoritesFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getFavorites()
            .doOnNext {
                tabDao.insertAll(it)
                tabViewDao.linkAllTabs(FAVORITES_ID, it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadFavoritesFromCache(): Observable<List<Tab>> {
        return tabViewDao.getTabViewTabs(FAVORITES_ID)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadTabsFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getAllTabInfo()
            .doOnNext {
                tabDao.insertAll(it)
                tabViewDao.linkAllTabs(NEW_ID, it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadTabsFromCache(): Observable<List<Tab>> {
        return tabViewDao.getTabViewTabs(NEW_ID)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}