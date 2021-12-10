package com.ppo.rssreader.services

import android.content.Context
import com.ppo.rssreader.models.RssItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CacheService{

    companion object {

        private const val itemsCache = "rss_cache"
        private const val urlCache = "url_cache"
        private const val CONTEXT_NAME = "AppCache"

        private val moshi = Moshi.Builder().build()
        private val adapter =
            moshi.adapter<List<RssItem>>(Types.newParameterizedType(List::class.java, RssItem::class.java))

        fun loadCachedUrl(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(urlCache, "").toString()
        }

        fun cacheRssItemsAndUrl(context: Context, data: List<RssItem>, url: String) {
            val sharedPreferences = context.getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
            val jsonRssItems = adapter.toJson(data)
            val editor = sharedPreferences.edit()
            editor.putString(itemsCache, jsonRssItems)
            editor.putString(urlCache, url)
            editor.apply()
        }

        fun loadCachedRssItems(context: Context): List<RssItem> {
            val sharedPreferences = context.getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
            val jsonRssItems = sharedPreferences.getString(itemsCache, null)
            return jsonRssItems?.let { adapter.fromJson(it) } ?: listOf<RssItem>()
        }
    }
}