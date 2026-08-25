package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_audioplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioplayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val selectedTrack = intent.getSerializableExtra("SELECTED_TRACK") as Track

        val backButton = findViewById<ImageView>(R.id.backButton)
        var trackImage = findViewById<ImageView>(R.id.trackImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val addToPlaylistButton = findViewById<ImageView>(R.id.addToPlaylistButton)
        val likeButton = findViewById<ImageView>(R.id.likeButton)
        val playButton = findViewById<ImageView>(R.id.playButton)
        val trackTimeProgress = findViewById<TextView>(R.id.trackTimeProgress)
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

        }

        addToPlaylistButton.setOnClickListener {

        }

        likeButton.setOnClickListener {

        }
    }
}