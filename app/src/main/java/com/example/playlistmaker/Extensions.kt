package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

fun AppCompatActivity.applyTheme() {
    val app = application as App
    if (app.darkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}