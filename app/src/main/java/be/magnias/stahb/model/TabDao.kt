package be.magnias.stahb.model

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TabDao {

    @Insert
    fun insert(tab: Tab)

    @Update
    fun update(tab: Tab)

    @Delete
    fun delete(tab: Tab)

    @Query("UPDATE tab_table SET favorite = 0 WHERE favorite = 1")
    fun revokeAllFavorites()

    @Query("DELETE FROM tab_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(tabs: List<Tab>)

    @Query("SELECT * FROM tab_table ORDER BY artist")
    fun getAllTabs(): Single<List<Tab>>

    @Query("SELECT * FROM tab_table WHERE favorite = 1 ORDER BY artist")
    fun getAllFavorites(): Single<List<Tab>>

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    fun getTab(id: String): Maybe<Tab>

    @Query("SELECT * FROM tab_table WHERE _id = :id AND last_update >= :timeout")
    fun hasTab(id: String, timeout: Int) : Int
}