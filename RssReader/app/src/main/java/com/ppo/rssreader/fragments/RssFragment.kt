package com.ppo.rssreader.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ppo.rssreader.R
import com.ppo.rssreader.models.RssViewModel
import com.ppo.rssreader.models.RssItem
import com.ppo.rssreader.adapters.RecyclerViewAdapter

class RssFragment(url: String) : Fragment() {
    private val RSS_FEED_LINK = url;
//    https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss
//    https://dev.by/rss
//    https://www.nba.com/jazz/rss.xml
//    https://www.utah.gov/whatsnew/rss.xml
    private var adapter: RecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()

    var recyclerView: RecyclerView ?= null
    private lateinit var rssViewModel: RssViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rss_list, container, false)

        recyclerView = view.findViewById(R.id.recycleView)
        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rssViewModel = ViewModelProvider(requireActivity())[RssViewModel::class.java]
        adapter = RecyclerViewAdapter(rssItems, activity)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter

        setUpRecyclerView(RSS_FEED_LINK)
    }

    private fun setUpRecyclerView(url: String) {
        rssViewModel.setUrl(url)
        activity?.let {
            rssViewModel.rssItems.observe(viewLifecycleOwner, { list ->
                adapter?.rssItems = list.toMutableList()
                adapter?.notifyDataSetChanged()
            })
        }
    }
}