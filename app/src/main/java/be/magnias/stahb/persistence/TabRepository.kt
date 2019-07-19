package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TabRepository(private val tabDao: TabDao) {
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun getTab(id: String): Observable<Tab> {
        return Observable.concat(
            loadTabFromCache(id),
            loadTabFromApi(id)
        )
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    private fun loadTabFromApi(id: String): Observable<Tab> {
        //Load tabs from network
        return stahbApi.getTab(id)
            .onErrorResumeNext(Observable.empty())
            .doOnNext {
                tabDao.insert(it)
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

}