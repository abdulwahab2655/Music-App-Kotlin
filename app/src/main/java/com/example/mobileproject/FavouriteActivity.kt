package com.example.mobileproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.io.IOException

class FavouriteActivity : AppCompatActivity() {

    private lateinit var favouriteRecyclerView: RecyclerView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var favouritesRef: DatabaseReference

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playPauseButtonM: ImageView
    private lateinit var nextButtonM: ImageView
    private lateinit var previousButtonM: ImageView
    private lateinit var favouriteButton: ImageView

    private lateinit var songTitleTextViewMinimized: TextView
    private lateinit var upButton: ImageView
    private var currentSongIndex: Int = -1
    private var isPlaying: Boolean = false

    private lateinit var albumCoverImageView: ImageView
    private lateinit var songTitleTextView: TextView
    private lateinit var songArtistTextView: TextView
    private lateinit var linearProgressBar: SeekBar
    private lateinit var startTextView: TextView
    private lateinit var endTextView: TextView
    private lateinit var playPauseButton: ImageView
    private lateinit var previousButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var downButton: ImageView
    private lateinit var playingSongLayout: LinearLayout
    private lateinit var controlsLayout: RelativeLayout
    private lateinit var seekBarHandler: Handler
    private lateinit var updateSeekBar: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        // Initialize views
        playPauseButtonM = findViewById(R.id.playPauseButtonMinimized)
        nextButtonM = findViewById(R.id.nextButtonMinimized)
        previousButtonM = findViewById(R.id.previousButtonMinimized)
        favouriteButton = findViewById(R.id.favouriteButton)
        songTitleTextViewMinimized = findViewById(R.id.songTitleTextViewMinimized)
        downButton = findViewById(R.id.downBtn)
        playingSongLayout = findViewById(R.id.playingSongLayout)
        controlsLayout = findViewById(R.id.controlsLayout)
        upButton = findViewById(R.id.upBtn)

        albumCoverImageView = findViewById(R.id.albumCoverImageView)
        songTitleTextView = findViewById(R.id.songTitleTextView)
        songArtistTextView = findViewById(R.id.songArtistTextView)
        linearProgressBar = findViewById(R.id.linearProgressBar)
        startTextView = findViewById(R.id.startTextView)
        endTextView = findViewById(R.id.endTextView)
        playPauseButton = findViewById(R.id.playPauseButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        mediaPlayer = MediaPlayer()

        // Database initialization
        database = FirebaseDatabase.getInstance()

        // Shared preferences
        val sharedPreference = getSharedPreferences(LoginActivity.preferenceNameText, Context.MODE_PRIVATE)
        val email = sharedPreference.getString(LoginActivity.emailText, null)

        if (email != null) {
            val userPath = email.replace(".", "_")
            val usersRef = database.getReference("Database").child("Users")
            val userRef = usersRef.child(userPath)
            favouritesRef = userRef.child("favorites")

            // Initialize RecyclerView
            favouriteRecyclerView = findViewById(R.id.recyclerView)
            initRecyclerView()
        } else {
            Toast.makeText(this, "User email not found.", Toast.LENGTH_SHORT).show()
            finish()
        }

        linearProgressBar = findViewById(R.id.linearProgressBar)
        // Initialize SeekBar handler and update runnable
        seekBarHandler = Handler()
        updateSeekBar = object : Runnable {
            override fun run() {
                try {
                    if (mediaPlayer.isPlaying) {
                        // Update SeekBar progress
                        linearProgressBar.progress = mediaPlayer.currentPosition
                        // Update TextViews for start and end time
                        startTextView.text = formatTime(mediaPlayer.currentPosition)
                        endTextView.text = formatTime(mediaPlayer.duration)
                    }
                } catch (e: IllegalStateException) {
                    // Handle the IllegalStateException (e.g., MediaPlayer not properly prepared)
                    e.printStackTrace()
                }

                // Run this Runnable every 100 milliseconds
                seekBarHandler.postDelayed(this, 100)
            }
        }

        linearProgressBar.max = mediaPlayer.duration

        linearProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Remove callbacks to stop updating SeekBar while tracking
                seekBarHandler.removeCallbacks(updateSeekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Start updating SeekBar after tracking ends
                seekBarHandler.postDelayed(updateSeekBar, 100)
            }
        })

        downButton.setOnClickListener {
            favouriteRecyclerView.visibility = View.VISIBLE
            // Create a slide-down animation for hiding the playingSongLayout
            val slideDown = TranslateAnimation(0f, 0f, 0f, playingSongLayout.height.toFloat())
            slideDown.duration = 500 // adjust duration as needed
            slideDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    playingSongLayout.visibility = View.GONE
                    controlsLayout.visibility = View.VISIBLE
                }
            })

            // Start the slide-down animation
            playingSongLayout.startAnimation(slideDown)

            isPlaying = mediaPlayer.isPlaying
            updateUI()

        }

        upButton.setOnClickListener {
            // Show the playingSongLayout
            playingSongLayout.visibility = View.VISIBLE

            // Create a slide-up animation for showing the playingSongLayout
            val slideUp = TranslateAnimation(0f, 0f, playingSongLayout.height.toFloat(), 0f)
            slideUp.duration = 500 // adjust duration as needed
            playingSongLayout.startAnimation(slideUp)

            // Update UI
            isPlaying = mediaPlayer.isPlaying
            updateUI()
            favouriteRecyclerView.visibility = View.GONE

        }
    }

    private fun initRecyclerView() {
        favouriteAdapter = FavouriteAdapter(this, emptyList())
        favouriteRecyclerView.adapter = favouriteAdapter
        favouriteRecyclerView.layoutManager = LinearLayoutManager(this)

        favouritesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favouritesList = mutableListOf<Favourite>()

                for (dataSnapshot in snapshot.children) {
                    val favourite = dataSnapshot.getValue(Favourite::class.java)
                    favourite?.let { favouritesList.add(it) }
                }

                favouriteAdapter.updateList(favouritesList)
                // Check if the list is empty, and if so, navigate to "No Data Found" activity
                if (favouritesList.isEmpty()) {
                    val intent = Intent(this@FavouriteActivity, NoDataFoundActivity::class.java)
                    startActivity(intent)
                    finish()  // Optional: Finish the current activity to prevent going back to an empty list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FavouriteActivity, "Failed to retrieve favourites.", Toast.LENGTH_SHORT).show()
            }
        })

        favouriteAdapter.onItemClickListener = object : FavouriteAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (currentSongIndex != position) {
                    currentSongIndex = position

                    // Slide-right animation for playingSongLayout
                    val slideRight = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f
                    )
                    slideRight.duration = 500 // Adjust duration as needed
                    playingSongLayout.startAnimation(slideRight)

                    playSong(favouriteAdapter.getFavouriteAtPosition(currentSongIndex))
                    updateUI()
                    playingSongLayout.visibility = View.VISIBLE
                    favouriteRecyclerView.visibility = View.GONE
                }
            }
        }
        playPauseButton.setOnClickListener {
            onPlayPauseClick(it)
        }

        nextButton.setOnClickListener {
            onNextClick(it)
        }

        previousButton.setOnClickListener {
            onPreviousClick(it)
        }

        mediaPlayer.setOnCompletionListener {
            onNextClick(nextButton)
        }

        playPauseButtonM.setOnClickListener {
            onPlayPauseClick(it)
        }
        favouriteButton.setOnClickListener{
            onFavouriteClick(it)
        }

        nextButtonM.setOnClickListener {
            onNextClick(it)
        }

        previousButtonM.setOnClickListener {
            onPreviousClick(it)
        }

        mediaPlayer.setOnCompletionListener {
            onNextClick(nextButtonM)
        }
    }

    private fun playSong(selectedData: Favourite) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(selectedData.songUrl)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true

            // Initialize SeekBar properties
            linearProgressBar.max = mediaPlayer.duration
            endTextView.text = formatTime(mediaPlayer.duration)

            // Start updating SeekBar progress
            seekBarHandler.postDelayed(updateSeekBar, 100)
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IOException
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            // Handle IllegalStateException
        }
    }


    private fun updateUI() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.pause)
            playPauseButtonM.setImageResource(R.drawable.pause)
        } else {
            playPauseButton.setImageResource(R.drawable.play)
            playPauseButtonM.setImageResource(R.drawable.playwhite)
        }

        val songTitle = favouriteAdapter.getSongTitleAtIndex(currentSongIndex)
        songTitleTextViewMinimized.text = songTitle
        songTitleTextView.text = songTitle
        songArtistTextView.text = favouriteAdapter.getArtistNameAtIndex(currentSongIndex)
        val imageResource=favouriteAdapter.getAlbumCoverUrlAtIndex(currentSongIndex)
        // Set data to views
        Picasso.get().load(imageResource).into(albumCoverImageView)
        // Check if the song is already a favorite
        val songIdString = favouriteAdapter.getSongId(currentSongIndex).toString()
        val favoriteIconResource =
            if (songIdString!=null) R.drawable.favorite_filled
            else R.drawable.favorite_empty
        favouriteButton.setImageResource(favoriteIconResource)

    }

    fun onPlayPauseClick(view: View) {
        if (isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        isPlaying = !isPlaying
        updateUI()
    }

    fun onNextClick(view: View) {
        if (favouriteAdapter.itemCount > 0) {
            if (currentSongIndex < favouriteAdapter.itemCount - 1) {
                currentSongIndex++
            } else {
                currentSongIndex = 0
            }
            playSong(favouriteAdapter.getFavouriteAtPosition(currentSongIndex))
            updateUI()
        }
    }

    fun onPreviousClick(view: View) {
        if (favouriteAdapter.itemCount > 0) {
            if (currentSongIndex > 0) {
                currentSongIndex--
            } else {
                currentSongIndex = favouriteAdapter.itemCount - 1
            }
            playSong(favouriteAdapter.getFavouriteAtPosition(currentSongIndex))
            updateUI()
        }
    }

    fun onFavouriteClick(view: View) {
        val songId = favouriteAdapter.getSongId(currentSongIndex).toString()
        val email = getEmailFromPreferences()

        if (email != null) {
            val database = FirebaseDatabase.getInstance()
            val rootRef = database.getReference("Database")
            val usersRef = rootRef.child("Users")
            val userNodeRef = usersRef.child(email.replace(".", "_"))
            val favoritesRef = userNodeRef.child("favorites").child(songId)

            favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Song is in favorites, remove it
                        favoritesRef.removeValue()
                        updateUI()
                        favouriteButton.setImageResource(R.drawable.favorite_empty)
                        Toast.makeText(view.context, "Removed from favorites.", Toast.LENGTH_SHORT).show()
                        onNextClick(view);
                    } else {
                        // Song is not in favorites
                        Toast.makeText(view.context, "Song is not in favorites.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(view.context, "Database error.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle case where email is null
            Toast.makeText(view.context, "Credentials not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEmailFromPreferences(): String? {
        val sharedPreference = getSharedPreferences(LoginActivity.preferenceNameText, Context.MODE_PRIVATE)
        return sharedPreference.getString(LoginActivity.emailText, null)
    }



    private fun formatTime(millis: Int): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        seekBarHandler.removeCallbacks(updateSeekBar)
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer.release()
    }
}
