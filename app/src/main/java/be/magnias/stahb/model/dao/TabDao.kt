package be.magnias.stahb.model.dao

import androidx.room.*
import be.magnias.stahb.model.Tab
import com.orhanobut.logger.Logger
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class TabDao {

    @Transaction
    open fun updateAll(favorites: List<Tab>, tabs: List<Tab>) {
        Logger.d("Updating database")
        removeAllFavorites()
        insertAll(tabs)
        insertAll(favorites)
        addFavorites(favorites.map { t -> t._id })
        setAllTabsUpdated()
    }

    @Insert
    abstract fun insert(tab: Tab)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIgnoreConflict(tab: Tab): Long

    @Update
    abstract fun update(tab: Tab)

    @Query("UPDATE tab_table SET artist = :artist, song = :song, tab = :tab, tuning = :tuning, loaded = :loaded WHERE _id = :id")
    abstract fun updateFields(id: String, artist: String, song: String, tab: String, tuning: String, loaded: Boolean)

    @Delete
    abstract fun delete(tab: Tab)

    @Query("DELETE FROM tab_table")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(tabs: List<Tab>)

    @Query("UPDATE tab_table SET favorite = 1, favoriteUpdated = 1 WHERE _id = :tabId")
    abstract fun addFavorite(tabId: String)

    @Query("UPDATE tab_table SET favorite = 0, favoriteUpdated = 1 WHERE _id = :tabId")
    abstract fun removeFavorite(tabId: String)

    @Query("UPDATE tab_table SET favorite = 0 WHERE favorite = 1")
    abstract fun removeAllFavorites()

    @Query("UPDATE tab_table SET favorite = 1 WHERE _id IN (:tabs)")
    abstract fun addFavorites(tabs: List<String>)

    @Query("SELECT * FROM tab_table WHERE favorite = 1")
    abstract fun getFavorites(): Observable<List<Tab>>

    @Query("SELECT * FROM tab_table ORDER BY artist")
    abstract fun getAllTabs(): Observable<List<Tab>>

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    abstract fun getTab(id: String): Observable<Tab>

    @Query("SELECT * FROM tab_table WHERE _id = :id AND last_update >= :timeout")
    abstract fun hasTab(id: String, timeout: Int) : Int

    @Query("SELECT * FROM tab_table WHERE favoriteUpdated = 1")
    abstract fun getAllUpdatedTabs(): Observable<List<Tab>>

    @Query("UPDATE tab_table SET favoriteUpdated = :updated WHERE _id = :tabId")
    abstract fun setTabUpdated(tabId: String, updated: Boolean)

    @Query("UPDATE tab_table SET favoriteUpdated = 0")
    abstract fun setAllTabsUpdated()
}