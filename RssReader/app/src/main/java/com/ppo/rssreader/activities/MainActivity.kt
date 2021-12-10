package com.ppo.rssreader.activities

import android.content.*
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.ppo.rssreader.R
import com.ppo.rssreader.fragments.RssFragment
import com.ppo.rssreader.network.NetworkStateReceiver
import com.ppo.rssreader.services.CacheService

class MainActivity : AppCompatActivity() {
    private lateinit var networkStateReceiver: NetworkStateReceiver
    private lateinit var iw: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iw = findViewById(R.id.icon)

        networkStateReceiver = NetworkStateReceiver(iw)
        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        findViewById<Button>(R.id.go_btn).setOnClickListener {
            insertRssFragment()
        }

        val rssUrl = CacheService.loadCachedUrl(this)
        findViewById<EditText>(R.id.url).setText(rssUrl)

        insertRssFragment()
    }

    private fun insertRssFragment() {
        val url = findViewById<EditText>(R.id.url).text.toString().trim()
        if(url != "https://pornhub.com/rss")
        {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.root_container, RssFragment(url))
                .commit()
        }
        else
        {
            Toast.makeText(this, "Incorrect site!", Toast.LENGTH_LONG).show()
        }
    }
}