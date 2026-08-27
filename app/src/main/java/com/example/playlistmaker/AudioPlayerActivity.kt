package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var selectedTrack : Track
    private lateinit var playButton : ImageView
    private lateinit var trackTimeProgress : TextView
    private var playbackHandler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable? = null


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioplayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        selectedTrack = intent.getSerializableExtra("SELECTED_TRACK") as Track
        preparePlayer()



        val backButton = findViewById<ImageView>(R.id.backButton)
        var trackImage = findViewById<ImageView>(R.id.trackImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val addToPlaylistButton = findViewById<ImageView>(R.id.addToPlaylistButton)
        val likeButton = findViewById<ImageView>(R.id.likeButton)
        playButton = findViewById(R.id.playButton)
        trackTimeProgress = findViewById(R.id.trackTimeProgress)
        val trackTimeData = findViewById<TextView>(R.id.trackTimeData)
        val albumData = findViewById<TextView>(R.id.albumData)
        val yearData = findViewById<TextView>(R.id.yearData)
        val genreData = findViewById<TextView>(R.id.genreData)
        val countryData = findViewById<TextView>(R.id.countryData)

        trackName.text = selectedTrack.trackName
        artistName.text = selectedTrack.artistName
        albumData.text = selectedTrack.collectionName
        Glide.with(this)
            .load(selectedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.artwork_placeholder)
            .into(trackImage)
        trackTimeData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(selectedTrack.trackTime)
        yearData.text = selectedTrack.releaseDate.substring(0,4)
        genreData.text = selectedTrack.primaryGenreName
        countryData.text = selectedTrack.country


        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        addToPlaylistButton.setOnClickListener {

        }

        likeButton.setOnClickListener {

        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        if (updateProgressRunnable != null) {
            playbackHandler.removeCallbacks(updateProgressRunnable!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(selectedTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play)
            trackTimeProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play)
            if (updateProgressRunnable != null) {
                playbackHandler.removeCallbacks(updateProgressRunnable!!)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause)
        updateProgressRunnable = Runnable {
            updateProgress()
        }
        playbackHandler.post(updateProgressRunnable!!)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play)
        if (updateProgressRunnable != null) {
            playbackHandler.removeCallbacks(updateProgressRunnable!!)
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateProgress() {
        trackTimeProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        if (mediaPlayer.isPlaying) {
            playbackHandler.postDelayed(updateProgressRunnable!!, 2000)
        }
    }
}