

package com.example.mobileproject

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FavouriteAdapter(
    private val context: Activity,
    private var favouriteList: List<Favourite>
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.songImage)
        val songName: TextView = itemView.findViewById(R.id.songName)
        val artistName: TextView = itemView.findViewById(R.id.artistName)
        val duration: TextView = itemView.findViewById(R.id.songDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false)
        return FavouriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentFavourite = favouriteList[position]

        holder.songName.text = currentFavourite.songName
        holder.artistName.text = currentFavourite.artistName

        val durationInSec = formatDuration(currentFavourite.duration)
        holder.duration.text = durationInSec

        Picasso.get().load(currentFavourite.songImgUrl.toUri()).placeholder(R.drawable.music)
            .into(holder.image)

        // Set the click listener
        holder.itemView.setOnClickListener {
            // Update the selected position
            selectedPosition = holder.adapterPosition
            onItemClickListener?.onItemClick(it, selectedPosition)
            notifyDataSetChanged() // Notify to update UI
        }

        // Highlight the selected item
        holder.itemView.isSelected = selectedPosition == position
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    fun updateList(newList: List<Favourite>) {
        favouriteList = newList
        notifyDataSetChanged()
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    var onItemClickListener: OnItemClickListener? = null

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getFavouriteAtPosition(position: Int): Favourite {
        return favouriteList[position]
    }

    fun getSongTitleAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < favouriteList.size) {
            favouriteList[index].songName
        } else {
            ""
        }
    }

    fun getArtistNameAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < favouriteList.size) {
            favouriteList[index].artistName
        } else {
            ""
        }
    }

    fun getSongUrl(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < favouriteList.size) {
            favouriteList[index].songUrl
        } else {
            ""
        }
    }

    fun getSongId(index: Int): Long {
        return if (index != RecyclerView.NO_POSITION && index < favouriteList.size) {
            favouriteList[index].songId
        } else {
            -1
        }
    }

    fun getAlbumCoverUrlAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < favouriteList.size) {
            favouriteList[index].songImgUrl
        } else {
            ""
        }
    }

    private fun formatDuration(durationInSeconds: Int): String {
        val minutes = durationInSeconds / 60
        val seconds = durationInSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
