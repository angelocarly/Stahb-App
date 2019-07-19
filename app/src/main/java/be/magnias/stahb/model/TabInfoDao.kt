package be.magnias.stahb.model

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TabInfoDao {

    @Insert
    fun insert(tab: TabInfo)

    @Update
    fun update(tab: TabInfo)

    @Delete
    fun delete(tab: TabInfo)

    @Query("DELETE FROM tab_info_table")
    fun deleteAll()

    @Query("UPDATE tab_info_table SET inFavoriteView = 0 WHERE inFavoriteView = 1")
    fun unlinkFavoriteTabs()

    @Query("UPDATE tab_info_table SET inFavoriteView = 1 WHERE _id in (:ids)")
    fun linkFavoriteTabs(ids: List<String>)

    @Query("UPDATE tab_info_table SET inNewView = 0 WHERE inNewView = 1")
    fun unlinkNewTabs()

    @Query("UPDATE tab_info_table SET inNewView = 1 WHERE _id in (:ids)")
    fun linkNewTabs(ids: List<String>)

    @Query("DELETE FROM tab_info_table WHERE inFavoriteView = 0 AND inNewView = 0 AND inRecentView = 0")
    fun removeUnusedTabs()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(tabs: List<TabInfo>)

    @Query("SELECT * FROM tab_info_table WHERE inFavoriteView = 1 ORDER BY artist")
    fun getAllFavorites(): Single<List<TabInfo>>

    @Query("SELECT * FROM tab_info_table WHERE inNewView = 1 ORDER BY artist")
    fun getAllNew(): Single<List<TabInfo>>

}