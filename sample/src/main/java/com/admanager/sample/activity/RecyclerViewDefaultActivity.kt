package com.admanager.sample.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.admanager.sample.R
import com.admanager.sample.adapter.TrackAdapter
import com.admanager.sample.adapter.TrackModel
import kotlinx.android.synthetic.main.activity_native_list.*
import java.util.*

/**
 * Created by Gust on 20.11.2018.
 */
class RecyclerViewDefaultActivity : AppCompatActivity() {

    var trackAdapter: TrackAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_list)
        trackAdapter = TrackAdapter(this)

        recyclerView.apply {
            val layout = LinearLayoutManager(this@RecyclerViewDefaultActivity)
            layoutManager = layout
            // set adapter
            adapter = trackAdapter
            // divider
            addItemDecoration(
                DividerItemDecoration(this@RecyclerViewDefaultActivity, layout.orientation)
            )
        }
        loadTracksAsync()
    }

    private fun loadTracksAsync() {
        trackAdapter!!.setLoadingFullScreen()
        Handler().postDelayed({
            val data = ArrayList<TrackModel?>()
            for (i in 0..49) {
                data.add(TrackModel(i, "Track_$i"))
            }
            trackAdapter!!.data = data
            trackAdapter!!.loaded()
        }, 2000)
    }
}