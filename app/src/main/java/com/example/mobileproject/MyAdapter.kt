package com.example.mobileproject

//import android.app.Activity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.net.toUri
//import androidx.recyclerview.widget.RecyclerView
//import com.squareup.picasso.Picasso
//
//class MyAdapter(val context: Activity, val dataList: List<Data>) :
//    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
//
//    // Variable to hold the selected position
//    private var selectedPosition: Int = RecyclerView.NO_POSITION
//
//    // ViewHolder class to hold the views
//    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        // Declare and initialize views here
//        val image: ImageView
//        val songName: TextView
//        val duration: TextView
//        val artistName: TextView
//
//        init {
//            image = itemView.findViewById(R.id.songImage)
//            songName = itemView.findViewById(R.id.songName)
//            duration = itemView.findViewById(R.id.songDuration)
//            artistName = itemView.findViewById(R.id.artistName)
//        }
//    }
//
//    // Create new views (invoked by the layout manager)
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false)
//        return MyViewHolder(itemView)
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        // populate data into View
//        val currentData = dataList[position]
//
//        holder.songName.text = currentData.title
//        holder.artistName.text = currentData.artist.name
//        val durationInSec = formatDuration(currentData.duration)
//        holder.duration.text = durationInSec
//
//        val coverUrl = currentData.album.cover
//
//        Picasso.get().load(coverUrl).placeholder(R.drawable.music).into(holder.image)
//
//        // Set the click listener
//        holder.itemView.setOnClickListener {
//            // Update the selected position
//            selectedPosition = holder.adapterPosition
//            onItemClickListener?.onItemClick(it, selectedPosition)
//            notifyDataSetChanged() // Notify to update UI
//        }
//
//        // Highlight the selected item
//        holder.itemView.isSelected = selectedPosition == position
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount(): Int {
//        return dataList.size
//    }
//
//    // Interface for item click
//    interface OnItemClickListener {
//        fun onItemClick(view: View, position: Int)
//    }
//
//    // Variable to hold the listener
//    var onItemClickListener: OnItemClickListener? = null
//
//
//    fun getDataAtPosition(position: Int): Data {
//        return dataList[position]
//    }
//
//    fun formatDuration(durationInSeconds: Int): String {
//        val minutes = durationInSeconds / 60
//        val seconds = durationInSeconds % 60
//        return String.format("%d:%02d", minutes, seconds)
//    }
//
//    fun getSongTitleAtIndex(index: Int): String {
//        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
//            dataList[index].title
//        } else {
//            ""
//        }
//    }
//    // Function to get artist name at a specific index
//    fun getArtistNameAtIndex(index: Int): String {
//        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
//            dataList[index].artist.name
//        } else {
//            ""
//        }
//    }
//
//    // Function to get album cover URL at a specific index
//    fun getAlbumCoverUrlAtIndex(index: Int): String {
//        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
//            dataList[index].album.cover
//        } else {
//            ""
//        }
//    }
//
//}



import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(val context: Activity, val dataList: List<Data>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Variable to hold the selected position
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // ViewHolder class to hold the views
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare and initialize views here
        val image: ImageView
        val songName: TextView
        val duration: TextView
        val artistName: TextView

        init {
            image = itemView.findViewById(R.id.songImage)
            songName = itemView.findViewById(R.id.songName)
            duration = itemView.findViewById(R.id.songDuration)
            artistName = itemView.findViewById(R.id.artistName)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // populate data into View
        val currentData = dataList[position]

        holder.songName.text = currentData.title
        holder.artistName.text = currentData.artist.name
        val durationInSec = formatDuration(currentData.duration)
        holder.duration.text = durationInSec

        val coverUrl = currentData.album.cover

        Picasso.get().load(coverUrl).placeholder(R.drawable.music).into(holder.image)

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

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Interface for item click
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    // Variable to hold the listener
    var onItemClickListener: OnItemClickListener? = null

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getDataAtPosition(position: Int): Data {
        return dataList[position]
    }

    fun formatDuration(durationInSeconds: Int): String {
        val minutes = durationInSeconds / 60
        val seconds = durationInSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun getSelectedSongTitle(): String {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            dataList[selectedPosition].title
        } else {
            ""
        }
    }
    fun getSongTitleAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].title
        } else {
            ""
        }
    }
    // Function to get artist name at a specific index
    fun getArtistNameAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].artist.name
        } else {
            ""
        }
    }
    // Function to get song url
    fun getSongUrl(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].preview
        } else {
            ""
        }
    }

    // Function to get song id
    fun getSongId(index: Int): Long {
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].id
        } else {
            -1
        }
    }

    // Function to get album cover URL at a specific index
    fun getAlbumCoverUrlAtIndex(index: Int): String {
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].album.cover
        } else {
            ""
        }
    }

    fun getSongDuration(index: Int): Int{
        return if (index != RecyclerView.NO_POSITION && index < dataList.size) {
            dataList[index].duration
        } else {
            -1
        }
    }
}

