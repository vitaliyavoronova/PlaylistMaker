package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<MaterialToolbar>(R.id.settings_back)
        backButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<MaterialTextView>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, ""))
        }

        val supportButton = findViewById<MaterialTextView>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mailto)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            startActivity(supportIntent)
        }

        val eulaButton = findViewById<MaterialTextView>(R.id.eula_button)
        eulaButton.setOnClickListener {
            val eulaIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.eula_url)))
            startActivity(eulaIntent)
        }
    }

}