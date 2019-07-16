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


class TabInfoAdapter : ListAdapter<Tab, TabInfoAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tab>() {
            override fun areItemsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem.artist == newItem.artist && oldItem.song == newItem.song
            }
        }
    }

    var onItemClick: ((Tab) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.tab_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTab: Tab = getItem(position)

        holder.textViewArtist.text = currentTab.artist
        holder.textViewSong.text = currentTab.song
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewArtist: TextView = itemView.text_view_artist
        var textViewSong: TextView = itemView.text_view_song

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }
}