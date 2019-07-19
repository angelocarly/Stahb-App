package be.magnias.stahb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab_info_table")
data class TabInfo (
    @PrimaryKey
    var _id: String,
    var artist: String,
    var song: String,
    var author: String?,
    var last_update: Int,
    var inNewView: Boolean,
    var inFavoriteView: Boolean,
    var inRecentView: Boolean
)