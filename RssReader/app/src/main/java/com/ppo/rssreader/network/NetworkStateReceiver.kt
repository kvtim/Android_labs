package com.ppo.rssreader.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import android.widget.Toast
import com.ppo.rssreader.R

class NetworkStateReceiver(iw: ImageView) : BroadcastReceiver() {
    private val imageView: ImageView = iw

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (isOnline(context)) {
                imageView.setImageResource(R.drawable.ic_wifi);
                Toast.makeText(context, "Connected to the Internet!", Toast.LENGTH_LONG).show()
            } else {
                imageView.setImageResource(R.drawable.ic_no_wifi);
                Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo?.isConnectedOrConnecting?:false
    }
}