package be.magnias.stahb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model for a tab.
 */
@Entity(tableName = "tab_table")
data class Tab (
    @PrimaryKey
    var _id: String,
    var artist: String,
    var song: String,
    var author: String?,
    var tab: String?,
    var tuning: String?,
    var favorite: Boolean = false,
    var favoriteUpdated: Boolean = false,
    var last_update: Int,
    var loaded: Boolean = false
)