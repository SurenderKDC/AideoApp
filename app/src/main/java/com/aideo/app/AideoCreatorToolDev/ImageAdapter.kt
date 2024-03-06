package com.android.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aideo.app.R
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class ImageAdapter(var context: Context, var arrayList: ArrayList<Image>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = arrayList[position].title
        holder.size.text = getSize(arrayList[position].size)
        Glide.with(context).load(arrayList[position].path)
            .placeholder(R.drawable.ic_baseline_broken_image_24).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var title: TextView
        var size: TextView

        init {
            imageView = itemView.findViewById(R.id.list_item_image)
            title = itemView.findViewById(R.id.list_item_title)
            size = itemView.findViewById(R.id.list_item_size)
        }
    }




    companion object {
        fun getSize(size: Long): String {
            if (size <= 0) {
                return "0"
            }
            val d = size.toDouble()
            val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
            val stringBuilder = StringBuilder()
            val decimalFormat = DecimalFormat("#,##0.#")
            val power = Math.pow(1024.0, log10.toDouble())
            stringBuilder.append(decimalFormat.format(d / power))
            stringBuilder.append(" ")
            stringBuilder.append(arrayOf("B", "KB", "MB", "GB", "TB")[log10])
            return stringBuilder.toString()
        }
    }
}