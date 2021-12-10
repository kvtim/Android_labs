package com.ppo.rssreader.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ppo.rssreader.R
import com.ppo.rssreader.models.RssItem
import com.ppo.rssreader.activities.WebViewActivity
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(
    var rssItems: List<RssItem>,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rss_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rssItems[position]
        holder.titleTV?.text = item.title
        holder.link?.text = item.link
        holder.content?.text  = item.description
        holder.pubDate?.text = item.pubDate

        if (item.enclosure.isNotEmpty()) {
            Picasso.get().load(item.enclosure).into(holder.img)
        }

        val link = item.enclosure

        if(link.isNotEmpty()) {
            holder.img?.visibility = View.VISIBLE
            context?.let {
                holder.img?.let { it1 ->
                    Glide.with(it)
                        .load(link)
                        .centerCrop()
                        .into(it1)
                }
            }
        }
        else {
            holder.img?.visibility = View.GONE
        }

        holder.itemView.tag = item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", item.link)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = rssItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView? = itemView.findViewById(R.id.txtTitle)

        val content: TextView? = itemView.findViewById(R.id.txtContent)
        val pubDate: TextView? = itemView.findViewById(R.id.txtPubdate)
        val link: TextView? = itemView.findViewById(R.id.txtLink)
        val img: ImageView? = itemView.findViewById(R.id.featuredImg)
    }
}