package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.TabInfo
import be.magnias.stahb.model.TabInfoDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabInfoRepository(private val tabInfoDao: TabInfoDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun getAllTabs(): Observable<List<TabInfo>> {
        return Observable.concat(
            loadTabsFromCache(),
            loadTabsFromApi())
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    fun getFavoriteTabs(): Observable<List<TabInfo>> {
        return Observable.concat(
            loadFavoritesFromCache(),
            loadFavoritesFromApi())
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    private fun loadFavoritesFromApi(): Observable<List<TabInfo>> {
        //Load tabs from network
        return stahbApi.getFavorites()
            .doOnNext {
                tabInfoDao.unlinkFavoriteTabs()
                tabInfoDao.insertIgnore(it)
                tabInfoDao.linkFavoriteTabs(it.map { t -> t._id })
                tabInfoDao.removeUnusedTabs()

                Logger.d("Dispatching favorites from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadFavoritesFromCache(): Observable<List<TabInfo>> {
        return tabInfoDao.getAllFavorites()
            .toObservable().filter { it.isNotEmpty() }
            .doOnNext {
                Logger.d("Dispatching tabs from database")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabsFromApi(): Observable<List<TabInfo>> {
        //Load tabs from network
        return stahbApi.getAllTabInfo()
            .doOnNext {
                tabInfoDao.unlinkNewTabs()
                tabInfoDao.insertIgnore(it)
                tabInfoDao.linkNewTabs(it.map { t -> t._id })
                tabInfoDao.removeUnusedTabs()

                Logger.d("Dispatching tabs from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabsFromCache(): Observable<List<TabInfo>> {
        return tabInfoDao.getAllNew()
            .toObservable().filter { it.isNotEmpty() }
            .doOnNext {
                Logger.d("Dispatching tabs from database")
            }
            .subscribeOn(Schedulers.io())
    }


}