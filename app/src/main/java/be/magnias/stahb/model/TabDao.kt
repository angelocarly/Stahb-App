package be.magnias.stahb.model

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single

@Dao
interface TabDao {

    @Insert
    fun insert(tab: Tab)

    @Update
    fun update(tab: Tab)

    @Delete
    fun delete(tab: Tab)

    @Query("DELETE FROM tab_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tabs: List<Tab>)

    @Query("SELECT * FROM tab_table ORDER BY artist")
    fun getAllTabs(): Single<List<Tab>>

    @Query("SELECT * FROM tab_table WHERE _id = :id")
    fun getTab(id: String): Single<Tab>

}