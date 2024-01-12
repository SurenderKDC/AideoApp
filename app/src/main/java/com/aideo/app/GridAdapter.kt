package com.aideo.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.aideo.app.CategoryList.Item

class GridAdapter(context: Context, private val itemList: List<Item>) : ArrayAdapter<Item>(context, 0, itemList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
            holder = ViewHolder()
            holder.itemImage = itemView.findViewById(R.id.grid_item_image)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        // Set the image for the grid item
        val itemImageRes = getItem(position)
        if (itemImageRes != null) {
            Glide.with(context).load(itemImageRes.imageUrl).into(holder.itemImage)
        }

        return itemView!!
    }

    private class ViewHolder {
        lateinit var itemImage: ImageView
    }
}
