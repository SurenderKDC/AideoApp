package com.aideo.app.Adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aideo.app.ApiCalling.ContentData
import com.aideo.app.R
import java.lang.Exception
import java.util.ArrayList


class StatusAdapter(var videos: ArrayList<ContentData>,var adapterPosition : Int, private val width : Int) : RecyclerView.Adapter<StatusAdapter.ViewHolder>()
{
    var currentPosition : Int = 0
    var widthData : Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.status_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.backText.width = (width / videos[adapterPosition].segments!!.size) - 14

        if(currentPosition == position && ((width / videos[adapterPosition].segments!!.size) - 14) >= widthData)
        {
            holder.upplerText.width = widthData.toInt()
            holder.upplerText.visibility = View.VISIBLE
        }
        else
        {
            holder.upplerText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = videos[adapterPosition].segments!!.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backText: TextView = itemView.findViewById(R.id.backText)
        val upplerText: TextView = itemView.findViewById(R.id.upperText)
    }


    fun allItemUpdateAdapter()
    {
        widthData = 0.0
        currentPosition = 0
        notifyDataSetChanged()
    }

    fun updateAdapter(position : Int, currentProgress : Double)
    {
        currentPosition = position
        widthData = currentProgress
        notifyItemChanged(position)
        notifyItemChanged(position - 1)


       try{
           notifyItemChanged(position + 1)
       }
       catch (e : Exception){}
    }


    fun updateProgress(position : Int, currentProgress : Double)
    {
        currentPosition = position
        widthData += currentProgress

        notifyItemChanged(position)
    }

}