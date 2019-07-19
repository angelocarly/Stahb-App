package be.magnias.stahb.model

import androidx.room.*

@Entity(tableName = "tab_view_tab_table",
    primaryKeys = ["tabId", "tabViewId"],
    foreignKeys = [
        ForeignKey(entity = Tab::class,
            parentColumns = ["_id"],
            childColumns = ["tabId"]),
        ForeignKey(entity = TabView::class,
            parentColumns = ["_id"],
            childColumns = ["tabViewId"])
    ])
data class TabViewTab (
    val tabViewId: String,
    val tabId: String
)