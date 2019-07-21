package be.magnias.stahb.model.dao

import androidx.room.*
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabView
import be.magnias.stahb.model.TabViewTab
import be.magnias.stahb.persistence.FAVORITES_ID
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class TabViewDao {

    @Insert
    abstract fun insert(tab: TabView)

    @Update
    abstract fun update(tab: TabView)

    @Delete
    abstract fun delete(tab: TabView)

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    abstract fun getTab(id: String): Single<Tab>

    @Query("SELECT * FROM tab_view_table WHERE _id = :id")
    abstract fun getTabView(id: String): Single<TabView>

    @Query("DELETE FROM tab_view_table")
    abstract fun deleteAll()

    @Insert
    abstract fun insert(tabViewTab: TabViewTab)

    @Insert
    abstract fun insertAll(tabViewTabs: List<TabViewTab>)

    @Transaction
    @Query("""
        SELECT * fROM tab_table t INNER JOIN tab_view_tab_table tvt ON t._id = tvt.tabId
        WHERE tvt.tabViewId = :id
    """)
    abstract fun getTabViewTabs(id: String): Flowable<List<Tab>>


    @Query("DELETE FROM tab_view_tab_table WHERE tabViewId = :tabViewId")
    abstract fun removeTabViewTabsByViewId(tabViewId: String)

    //Remove all relations between
    @Transaction
    open fun linkAllTabs(tabViewId: String, tabs: List<Tab>) {
        removeTabViewTabsByViewId(tabViewId)
        insertAll(tabs.map { t -> TabViewTab(tabViewId, t._id) })
    }

}