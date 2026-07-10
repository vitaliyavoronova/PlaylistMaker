package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        loadTheme()
    }

    private fun loadTheme() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        darkTheme = if (sharedPref.contains("dark_theme_key")) {
            sharedPref.getBoolean("dark_theme_key", false)
        } else {
            isSystemDarkTheme()
        }
    }


    fun saveTheme(isDark: Boolean) {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit {
            putBoolean("dark_theme_key", isDark)
        }
    }

    private fun isSystemDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES == Configuration.UI_MODE_NIGHT_YES
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}