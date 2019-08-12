package be.magnias.stahb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.magnias.stahb.R
import be.magnias.stahb.model.Tab
import kotlinx.android.synthetic.main.tab_item.view.*

/**
 * TabAdapter for a recyclerView
 * Fills a list with general info of the tabs.
 */
class TabAdapter : ListAdapter<Tab, TabAdapter.ViewHolder>(DIFF_CALLBACK) {

    /**
     * Provides item indexing for the Adapter.
     */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tab>() {
            override fun areItemsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem.artist == newItem.artist && oldItem.song == newItem.song && oldItem._id == newItem._id && oldItem.loaded == newItem.loaded
            }
        }
    }

    /**
     * Unit to listen to any clicked entry
     */
    var onItemClick: ((Tab) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.tab_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTab: Tab = getItem(position)

        // Set the data of a single tab entry
        holder.textViewArtist.text = currentTab.artist
        holder.textViewSong.text = currentTab.song
        if(currentTab.loaded) holder.textViewCached.visibility = View.VISIBLE
        else holder.textViewCached.visibility = View.GONE
    }

    /**
     * ViewHolder to store the data used in each Tab entry
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewArtist: TextView = itemView.text_view_artist
        var textViewSong: TextView = itemView.text_view_song
        var textViewCached: TextView = itemView.text_view_cached

        init {
            // Invoke the onItemClick Unit when this entry is clicked
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }
}