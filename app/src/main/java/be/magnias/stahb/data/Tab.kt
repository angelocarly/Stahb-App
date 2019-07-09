package be.magnias.stahb.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab_table")
data class Tab(
    var artist: String,
    var song: String,
    var author: String,
    var tab: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}