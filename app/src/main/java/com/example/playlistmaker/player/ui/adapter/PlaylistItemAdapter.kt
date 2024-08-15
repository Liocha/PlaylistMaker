package com.example.playlistmaker.player.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist


class PlaylistItemAdapter(
    private val getCountEnding: (Int) -> String,
    private val onClick: (Playlist) -> Unit
) :
    Adapter<PlaylistItemAdapter.PlaylistItemViewHolder>() {

    private var playlists = listOf<Playlist>()

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistItemViewHolder(rootView: View, private val getCountEnding: (Int) -> String) :
        ViewHolder(rootView) {
        private val name: TextView
        private val count: TextView
        private val cover: ImageView

        init {
            name = rootView.findViewById(R.id.name)
            count = rootView.findViewById(R.id.tracks_count)
            cover = rootView.findViewById(R.id.cover)
        }

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            count.text = "${playlist.tracksCount} ${getCountEnding(playlist.tracksCount)}"
            if (!playlist.pathCover.isNullOrEmpty()) {
                val imageUri = Uri.parse(playlist.pathCover)
                cover.setImageURI(imageUri)
            } else {
                cover.setImageResource(R.drawable.placeholder_artwork)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistItemViewHolder(view, getCountEnding)
    }

    override fun onBindViewHolder(holder: PlaylistItemViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClick(playlists[position])
        }

    }

    override fun getItemCount() = playlists.size
}