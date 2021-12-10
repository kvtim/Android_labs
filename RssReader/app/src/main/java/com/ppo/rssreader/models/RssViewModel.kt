package com.ppo.rssreader.models

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ppo.rssreader.services.CacheService
import com.prof.rssparser.Parser
import kotlinx.coroutines.launch

class RssViewModel(private val app: Application): AndroidViewModel(app) {

    val rssItems = MutableLiveData<List<RssItem>>()
    private var url: String

    init {
        val string = CacheService.loadCachedUrl(app.applicationContext)
        url = string
        getRssItems()
    }

    private fun getRssItems() = viewModelScope.launch {
        if (isOnline()) {
            rssItems.value = try {
                val parser = Parser.Builder()
                    .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
                    .build()
                parser.getChannel(url).articles.map {
                    RssItem(
                        title = it.title ?: "",
                        description = it.description ?: "",
                        enclosure = it.image ?: "",
                        pubDate = it.pubDate ?: "",
                        link = it.link ?: ""
                    )
                }
            } catch (e: Exception) {
                listOf()
            }
            if (rssItems.value!!.isNotEmpty()) {
                CacheService.cacheRssItemsAndUrl(
                    app.applicationContext,
                    rssItems.value!!.take(10),
                    url
                )
            } else {
                Toast.makeText(app.applicationContext, "Incorrect RSS URL!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(app.applicationContext, "You're in offline mode!", Toast.LENGTH_SHORT).show()
            rssItems.value = CacheService.loadCachedRssItems(app.applicationContext)
        }
    }

    private fun isOnline(): Boolean {
        val manager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting?:false
    }

    fun setUrl(string: String) {
        url = string
        getRssItems()
    }
}