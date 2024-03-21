package com.example.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.data.api.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryItemAdapter(private val dataSet: MutableList<Track>) :
    Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    class HistoryItemViewHolder(rootView: View) : ViewHolder(rootView) {
        private val artistName: TextView
        private val trackName: TextView
        private val trackTime: TextView
        private val artworkUrl100: ImageView
        private val radiusInPx =
            rootView.context.resources.getDimension(R.dimen.corner_radius_search_item_img).toInt()

        init {
            trackName = rootView.findViewById(R.id.track_name)
            artistName = rootView.findViewById(R.id.artist_name)
            trackTime = rootView.findViewById(R.id.track_time)
            artworkUrl100 = rootView.findViewById(R.id.artwork_url_100)
        }

        fun bind(track: Track) {
            artistName.text = track.artistName
            trackName.text = track.trackName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            Glide.with(artworkUrl100).load(track.artworkUrl100).centerCrop().transform(
                RoundedCorners(radiusInPx)
            )
                .placeholder(R.drawable.placeholder_artwork).into(artworkUrl100)

            itemView.setOnClickListener {
                val displayIntent = Intent(itemView.context, AudioplayerActivity::class.java).apply {
                    putExtra("TRACK", track)
                }
                ContextCompat.startActivity(itemView.context, displayIntent, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}