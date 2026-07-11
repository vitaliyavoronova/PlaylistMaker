package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var searchHistory: SearchHistory
    private val adapter = TrackAdapter(tracks) { clickedTrack ->
        searchHistory.saveTrack(clickedTrack)
    }

    private lateinit var searchHistoryView: LinearLayout
    private lateinit var searchHistoryTracklist: RecyclerView
    private lateinit var cleanHistory: Button

    private fun updateHistoryView() {
        val history = searchHistory.getHistory()

        if (history.isEmpty()) {
            searchHistoryView.visibility = View.GONE
            return
        }

        searchHistoryView.visibility = View.VISIBLE

        searchHistoryTracklist.adapter = TrackAdapter(history) { track ->
            searchHistory.saveTrack(track)
            updateHistoryView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.tracklist)
        val emptyTrackList = findViewById<TextView>(R.id.emptyTracklist)
        val errorTrackList = findViewById<LinearLayout>(R.id.errorTracklist)
        val inputSearch = findViewById<EditText>(R.id.searchMain)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val backButton = findViewById<MaterialToolbar>(R.id.searchBack)
        val searchRefresh = findViewById<Button>(R.id.searchRefresh)
        searchHistory = SearchHistory(getSharedPreferences("app_prefs", MODE_PRIVATE))
        recyclerView.adapter = adapter

        searchHistoryView = findViewById(R.id.searchHistory)
        searchHistoryTracklist = findViewById(R.id.searchHistoryTracklist)
        cleanHistory = findViewById(R.id.cleanHistory)

        updateHistoryView()

        cleanHistory.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryView()
        }

        inputSearch.setText(searchText)

        inputSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputSearch.text.isEmpty()) {
                updateHistoryView()
            } else {
                searchHistoryView.visibility = View.GONE
            }
        }

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputSearch.text.isNotEmpty()) {
                    apiSearch(inputSearch, recyclerView, emptyTrackList, errorTrackList)
                }
            }
            false
        }

        searchRefresh.setOnClickListener {
            apiSearch(inputSearch, recyclerView, emptyTrackList, errorTrackList)
        }

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputSearch.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            tracks.clear()
            emptyTrackList.visibility = View.GONE
            errorTrackList.visibility = View.GONE
            adapter.notifyDataSetChanged()
            updateHistoryView()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                s?.let {
                    searchText = it.toString()
                    if (searchText.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyTrackList.visibility = View.GONE
                        errorTrackList.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        inputSearch.addTextChangedListener(simpleTextWatcher)

    }




    private fun apiSearch(inputSearch : EditText, recycler : RecyclerView, empty : TextView, error : LinearLayout) {
        searchHistoryView.visibility = View.GONE
        iTunesService.search(inputSearch.text.toString()).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        recycler.visibility = View.VISIBLE
                        empty.visibility = View.GONE
                        error.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) {
                        recycler.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                        error.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    recycler.visibility = View.GONE
                    empty.visibility = View.GONE
                    error.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<TracksResponse?>, t: Throwable) {
                recycler.visibility = View.GONE
                empty.visibility = View.GONE
                error.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
            }
        })
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""
    }
    var searchText : String = ""
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)
    }

}



