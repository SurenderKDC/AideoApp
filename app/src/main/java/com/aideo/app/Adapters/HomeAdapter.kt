package com.aideo.app.Adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aideo.app.*
import com.aideo.app.Models.Topic


class InnerRecyclerViewAdapter(private val items: Topic) :
    RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_inner_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.content[position]

        Log.d("MainActivity image suri", "$baseUrlForImage/"+item.thumbnail)

        Glide.with(holder.itemView.context).load("$baseUrlForImage"+item.thumbnail).into(holder.imageView)

        holder.title.text = item.title

        holder.imageView.setOnClickListener{
            val intent = Intent(holder.itemView.context, HemePlayerScreen::class.java)
            intent.putExtra("key","${item.contentId}")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.content.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        var title : TextView = itemView.findViewById(R.id.title)
    }
}



class OuterRecyclerViewAdapter(private val topics: ArrayList<Topic>) :
    RecyclerView.Adapter<OuterRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_outter_layout_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topics[position]

        holder.topicTextView.text = topic.name

        holder.innerRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context,LinearLayoutManager.HORIZONTAL, false)
        holder.innerRecyclerView.adapter = InnerRecyclerViewAdapter(topic)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicTextView: TextView = itemView.findViewById(R.id.topicTextView)
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)
    }

    fun updateData(newData: List<Topic>) {
        topics.clear()
        topics.addAll(newData)
        notifyDataSetChanged()
    }

}
