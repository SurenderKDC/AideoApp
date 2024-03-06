package com.aideo.app.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.aideo.app.ApiCalling.ContentData
import com.aideo.app.databinding.ListVideoBinding

class VideoAdapter(
    var context: Context,
    var videos: ArrayList<Int>,
    private val itemClickListener: ClickFunctionality
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    private var textValue: String = ""

    class VideoViewHolder(
        val binding: ListVideoBinding,
        var context: Context,

    )
        : RecyclerView.ViewHolder(binding.root)
    {
        var simpleVideoView: VideoView? = null
    }

    fun updateTextValue(newValue: String) {
        textValue = newValue
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = ListVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val model = videos[position]

        Log.d("notice position", "$position")

        try
        {
            holder.binding.tvTitle.text = "$textValue"
            holder.binding.imageView.visibility = View.GONE
        }
        catch (e : Exception){
        }

        holder.binding.leftTap.setOnClickListener{
            itemClickListener.clickOnLeftSide()
        }


        holder.binding.rightTap.setOnClickListener{
            itemClickListener.clickOnRightSide()
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

}