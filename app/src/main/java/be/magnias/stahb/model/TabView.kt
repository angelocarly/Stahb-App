package be.magnias.stahb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab_view_table")
data class TabView (
    @PrimaryKey
    var _id: String
)