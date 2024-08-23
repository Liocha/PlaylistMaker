package com.example.playlistmaker.media.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistCollectionItemAdapter(
    private val getCountEnding: (tracksCount: Int) -> String,
    private val onClick: (playlistId: Int) -> Unit
) : Adapter<PlaylistCollectionItemAdapter.PlaylistCollectionItemViewHolder>() {

    private var playlistCollection = listOf<Playlist>()

    fun updateData(newPlaylistCollection: List<Playlist>) {
        playlistCollection = newPlaylistCollection
        notifyDataSetChanged()
    }


    class PlaylistCollectionItemViewHolder(
        rootView: View,
        private val getCountEnding: (Int) -> String
    ) : ViewHolder(rootView) {
        private val name: TextView = rootView.findViewById(R.id.name)
        private val count: TextView = rootView.findViewById(R.id.count)
        private val cover: ImageView = rootView.findViewById(R.id.cover)

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            count.text = "${playlist.tracksCount} ${getCountEnding(playlist.tracksCount)}"

            if (playlist.pathCover == null) {
                cover.setImageResource(R.drawable.placeholder_artwork)
            } else {
                cover.setImageURI(playlist.pathCover)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaylistCollectionItemViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_collection_item, parent, false)
        return PlaylistCollectionItemViewHolder(view, getCountEnding)
    }

    override fun onBindViewHolder(holder: PlaylistCollectionItemViewHolder, position: Int) {
        holder.bind(playlistCollection[position])
        holder.itemView.setOnClickListener {
            onClick(playlistCollection[position].id)
        }
    }

    override fun getItemCount() = playlistCollection.size

}
