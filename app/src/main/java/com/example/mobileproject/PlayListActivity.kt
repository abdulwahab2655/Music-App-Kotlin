package com.example.mobileproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.LoginActivity.Companion.preferenceNameText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PlayListActivity : AppCompatActivity() {

    private lateinit var myAdapter: MyAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playPauseButtonM: ImageView
    private lateinit var nextButtonM: ImageView
    private lateinit var previousButtonM: ImageView
    private lateinit var favouriteButton: ImageView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var favouriteFolderBtn: ImageButton

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
    private lateinit var recyclerView: RecyclerView
    private lateinit var seekBarHandler: Handler
    private lateinit var updateSeekBar: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)

        playPauseButtonM = findViewById(R.id.playPauseButtonMinimized)
        nextButtonM = findViewById(R.id.nextButtonMinimized)
        previousButtonM = findViewById(R.id.previousButtonMinimized)
        favouriteButton = findViewById(R.id.favouriteButton)
        songTitleTextViewMinimized = findViewById(R.id.songTitleTextViewMinimized)
        downButton = findViewById(R.id.downBtn)
        playingSongLayout = findViewById(R.id.playingSongLayout)
        controlsLayout = findViewById(R.id.controlsLayout)
        recyclerView = findViewById(R.id.recyclerView)
        upButton = findViewById(R.id.upBtn)
        favouriteFolderBtn=findViewById(R.id.favouriteFolderBtn)

        albumCoverImageView = findViewById(R.id.albumCoverImageView)
        songTitleTextView = findViewById(R.id.songTitleTextView)
        songArtistTextView = findViewById(R.id.songArtistTextView)
        linearProgressBar = findViewById(R.id.linearProgressBar)
        startTextView = findViewById(R.id.startTextView)
        endTextView = findViewById(R.id.endTextView)
        playPauseButton = findViewById(R.id.playPauseButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)

        favouriteFolderBtn.setOnClickListener{
            val intent = Intent(this@PlayListActivity, FavouriteActivity::class.java)
            startActivity(intent)
            finish()
        }

        downButton.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
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
            recyclerView.visibility = View.GONE

        }


        mediaPlayer = MediaPlayer()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        // Retrieving the intent
        val intent = intent
        val artistName = intent.getStringExtra("Artist")

        val call = apiInterface.getData(artistName.toString())
        call.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                if (response.isSuccessful) {
                    val myData = response.body()
                    val dataList = myData?.data ?: emptyList()
                    setupRecyclerView(dataList)
                } else {
                    Toast.makeText(this@PlayListActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Toast.makeText(this@PlayListActivity, "No Data found! Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })

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
    }

    private fun setupRecyclerView(dataList: List<Data>) {
        myAdapter = MyAdapter(this, dataList)

        myAdapter.onItemClickListener = object : MyAdapter.OnItemClickListener {
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

                    playSong(dataList[currentSongIndex])
                    updateUI()
                    playingSongLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

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

        nextButtonM.setOnClickListener {
            onNextClick(it)
        }

        previousButtonM.setOnClickListener {
            onPreviousClick(it)
        }
        favouriteButton.setOnClickListener {
            onFavouriteClick(it)
        }

        mediaPlayer.setOnCompletionListener {
            onNextClick(nextButtonM)
        }

        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun playSong(selectedData: Data) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(selectedData.preview)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true

            // Initialize SeekBar properties
            linearProgressBar.max = mediaPlayer.duration
            endTextView.text = formatTime(mediaPlayer.duration)

            // Start updating SeekBar progress
            seekBarHandler.postDelayed(updateSeekBar, 100)
        } catch (e: Exception) {
            e.printStackTrace()
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

        val songTitle = myAdapter.getSongTitleAtIndex(currentSongIndex)
        songTitleTextViewMinimized.text = songTitle
        songTitleTextView.text = songTitle
        songArtistTextView.text = myAdapter.getArtistNameAtIndex(currentSongIndex)
        val imageResource = myAdapter.getAlbumCoverUrlAtIndex(currentSongIndex)
        // Set data to views
        Picasso.get().load(imageResource).into(albumCoverImageView)
        val songIdString = myAdapter.getSongId(currentSongIndex).toString()

// Use the isSongInFavorites function with a callback
        isSongInFavorites(songIdString) { isInFavorites ->
            // This code will be executed when the result is available
            val favoriteIconResource =
                if (isInFavorites) R.drawable.favorite_filled
                else R.drawable.favorite_empty
            favouriteButton.setImageResource(favoriteIconResource)
        }

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
        if (currentSongIndex < myAdapter.itemCount - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        playSong(myAdapter.getDataAtPosition(currentSongIndex))
        updateUI()
    }

    fun onPreviousClick(view: View) {
        if (currentSongIndex > 0) {
            currentSongIndex--
        } else {
            currentSongIndex = myAdapter.itemCount - 1
        }
        playSong(myAdapter.getDataAtPosition(currentSongIndex))
        updateUI()
    }

    fun onFavouriteClick(view: View) {
        val sharedPreference =
            view.context.getSharedPreferences(
                LoginActivity.preferenceNameText,
                Context.MODE_PRIVATE
            )

        // Get user credentials
        val email = sharedPreference.getString(LoginActivity.emailText, null)

        // Get song details
        val songId = myAdapter.getSongId(currentSongIndex)
        val songTitle = myAdapter.getSongTitleAtIndex(currentSongIndex)
        val imageResource = myAdapter.getAlbumCoverUrlAtIndex(currentSongIndex)
        val artistName = myAdapter.getArtistNameAtIndex(currentSongIndex)
        val songUrl = myAdapter.getSongUrl(currentSongIndex)
        val duration = myAdapter.getSongDuration(currentSongIndex)

        if (email != null) {
            // Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()

            // Reference to the root node
            val rootRef = database.getReference("Database")

            // Reference to the 'Users' node under the root node
            val usersRef = rootRef.child("Users")

            // Reference to the user's node based on email
            val userNodeRef = usersRef.child(email.replace(".", "_"))

            // Reference to the 'favorites' node under the user's node
            val favoritesRef = userNodeRef.child("favorites")

            // Check if the song is already a favorite
            val songIdString = songId.toString()
            val favoriteSongRef = favoritesRef.child(songIdString)

            favoriteSongRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Song is already a favorite, remove it
                        favoriteSongRef.removeValue()
                        // Update the icon to favorite_empty
                        favouriteButton.setImageResource(R.drawable.favorite_empty)
                        Toast.makeText(
                            view.context,
                            "Removed from favorites.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Song is not a favorite, add it
                        val songMap = mapOf(
                            "songId" to songId,
                            "songName" to songTitle,
                            "songImgUrl" to imageResource,
                            "artistName" to artistName,
                            "songUrl" to songUrl,
                            "duration" to duration
                        )

                        favoritesRef.child(songIdString).setValue(songMap)
                        // Update the icon to favorite_filled
                        favouriteButton.setImageResource(R.drawable.favorite_filled)
                        Toast.makeText(
                            view.context,
                            "Added to favorites.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(view.context, "Database error.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case where credentials are not available
            Toast.makeText(view.context, "Credentials not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isSongInFavorites(songId: String, callback: (Boolean) -> Unit) {
        val sharedPreference =
            getSharedPreferences(LoginActivity.preferenceNameText, Context.MODE_PRIVATE)
        val email = sharedPreference.getString(LoginActivity.emailText, null)

        if (email != null) {
            val database = FirebaseDatabase.getInstance()
            val rootRef = database.getReference("Database")
            val usersRef = rootRef.child("Users")
            val userNodeRef = usersRef.child(email.replace(".", "_"))
            val favoritesRef = userNodeRef.child("favorites")

            favoritesRef.child(songId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Use the callback to pass the result
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    callback(false)
                }
            })
        } else {
            // Handle case where email is null
            callback(false)
        }
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
