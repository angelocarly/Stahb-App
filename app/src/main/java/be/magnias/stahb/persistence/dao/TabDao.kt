package be.magnias.stahb.persistence.dao

import androidx.room.*
import be.magnias.stahb.model.Tab
import com.orhanobut.logger.Logger
import io.reactivex.Observable

/**
 * Room Dao for Tabs.
 * Provides SQL statements to access the local SQL lite db.
 */
@Dao
abstract class TabDao {

    // Select
    @Query("SELECT * FROM tab_table ORDER BY artist")
    abstract fun getAllTabs(): Observable<List<Tab>>

    @Query("SELECT * FROM tab_table WHERE favorite = 1")
    abstract fun getFavorites(): Observable<List<Tab>>

    // Insert
    @Insert
    abstract fun insert(tab: Tab)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIgnoreConflict(tab: Tab): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(tabs: List<Tab>)

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    abstract fun getTab(id: String): Observable<Tab>

    @Query("SELECT * FROM tab_table WHERE _id = :id AND last_update >= :timeout")
    abstract fun hasTab(id: String, timeout: Int) : Int

    @Query("SELECT * FROM tab_table WHERE favoriteUpdated = 1")
    abstract fun getAllUpdatedTabs(): Observable<List<Tab>>

    // Update
    @Update
    abstract fun update(tab: Tab)

    @Query("UPDATE tab_table SET artist = :artist, song = :song, tab = :tab, tuning = :tuning, loaded = :loaded WHERE _id = :id")
    abstract fun updateFields(id: String, artist: String, song: String, tab: String, tuning: String, loaded: Boolean)

    /**
     * Replace the entire database.
     */
    @Transaction
    open fun updateAll(favorites: List<Tab>, tabs: List<Tab>) {
        Logger.d("Updating database")
        removeAllFavorites()
        insertAll(tabs)
        insertAll(favorites)
        addFavorites(favorites.map { t -> t._id })
        setAllTabsUpdated()
    }

    @Query("UPDATE tab_table SET favoriteUpdated = :updated WHERE _id = :tabId")
    abstract fun setTabUpdated(tabId: String, updated: Boolean)

    @Query("UPDATE tab_table SET favoriteUpdated = 0")
    abstract fun setAllTabsUpdated()

    @Query("UPDATE tab_table SET favorite = 1, favoriteUpdated = 1 WHERE _id = :tabId")
    abstract fun addFavorite(tabId: String)

    @Query("UPDATE tab_table SET favorite = 0, favoriteUpdated = 1 WHERE _id = :tabId")
    abstract fun removeFavorite(tabId: String)

    @Query("UPDATE tab_table SET favorite = 0 WHERE favorite = 1")
    abstract fun removeAllFavorites()

    @Query("UPDATE tab_table SET favorite = 1 WHERE _id IN (:tabs)")
    abstract fun addFavorites(tabs: List<String>)

    //Delete
    @Delete
    abstract fun delete(tab: Tab)

    @Query("DELETE FROM tab_table")
    abstract fun deleteAll()

}