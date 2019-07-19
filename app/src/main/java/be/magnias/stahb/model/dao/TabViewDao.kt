package be.magnias.stahb.model.dao

import androidx.room.*
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabView
import be.magnias.stahb.model.TabViewTab
import io.reactivex.Single

@Dao
interface TabViewDao {

    @Insert
    fun insert(tab: TabView)

    @Update
    fun update(tab: TabView)

    @Delete
    fun delete(tab: TabView)

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    fun getTab(id: String): Single<Tab>

    @Query("SELECT * FROM tab_view_table WHERE _id = :id")
    fun getTabView(id: String): Single<TabView>

    @Query("DELETE FROM tab_view_table")
    fun deleteAll()

    @Insert
    fun insert(tabViewTab: TabViewTab)

    @Insert
    fun linkTabs(tabViewTabs: List<TabViewTab>)

    @Transaction
    @Query("""
        SELECT * fROM tab_table t INNER JOIN tab_view_tab_table tvt ON t._id = tvt.tabId
        WHERE tvt.tabViewId = :id
    """)
    fun getTabViewTabs(id: String): Single<List<Tab>>


    @Query("DELETE FROM tab_view_tab_table WHERE tabViewId = :tabViewId")
    fun removeTabViewTabsByViewId(tabViewId: String)

}