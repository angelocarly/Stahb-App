package be.magnias.stahb.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TabDao {

    @Insert
    fun insert(tab: Tab)

    @Update
    fun update(tab: Tab)

    @Delete
    fun delete(tab: Tab)

    @Query("SELECT * FROM tab_table ORDER BY artist")
    fun getAllTabs(): LiveData<List<Tab>>

}