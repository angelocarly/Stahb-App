package be.magnias.stahb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.magnias.stahb.R
import be.magnias.stahb.data.Tab
import kotlinx.android.synthetic.main.tab_item.view.*


class TabAdapter : ListAdapter<Tab, TabAdapter.TabHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tab>() {
            override fun areItemsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tab, newItem: Tab): Boolean {
                return oldItem.artist == newItem.artist && oldItem.song == newItem.song
            }
        }
    }

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.tab_item, parent, false)
        return TabHolder(itemView)
    }

    override fun onBindViewHolder(holder: TabHolder, position: Int) {
        val currentTab: Tab = getItem(position)

        holder.textViewArtist.text = currentTab.artist
        holder.textViewSong.text = currentTab.song
    }

    fun getTabAt(position: Int): Tab {
        return getItem(position)
    }

    inner class TabHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(getItem(position))
                }
            }
        }

        var textViewArtist: TextView = itemView.text_view_artist
        var textViewSong: TextView = itemView.text_view_song
    }

    interface OnItemClickListener {
        fun onItemClick(tab: Tab)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}