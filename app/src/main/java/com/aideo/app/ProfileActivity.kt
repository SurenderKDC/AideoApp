package com.aideo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.aideo.app.CategoryList.Item

class ProfileActivity : AppCompatActivity() {
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        gridView = findViewById(R.id.posts_grid)

        val itemList = getItemList()
        val adapter = GridAdapter(this, itemList)
        gridView.adapter = adapter
    }

    private fun getItemList(): List<Item> {
        // Return a list of items
        // You can fetch the data from an API, a database, or any other source
        // For simplicity, I'm returning a hardcoded list here
        return listOf(
            Item("https://images.pexels.com/photos/1629781/pexels-photo-1629781.jpeg?auto=compress&cs=tinysrgb&w=1600"),
            Item("https://images.pexels.com/photos/163036/mario-luigi-yoschi-figures-163036.jpeg?auto=compress&cs=tinysrgb&w=1600"),
            Item("https://images.pexels.com/photos/209037/pexels-photo-209037.jpeg?auto=compress&cs=tinysrgb&w=1600"),
            Item("https://thumbs.dreamstime.com/b/honeymoon-couple-romantic-love-beach-sunset-34259129.jpg"),
            Item("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT6jysTmMIDAO5dwm3UfZYD4lo0-ymrzpYutQ&usqp=CAU"),
            Item("https://images.pexels.com/photos/209037/pexels-photo-209037.jpeg?auto=compress&cs=tinysrgb&w=1600"),
            Item("https://images.pexels.com/photos/209037/pexels-photo-209037.jpeg?auto=compress&cs=tinysrgb&w=1600"),
            Item("https://images.squarespace-cdn.com/content/v1/5b398fb2f793925040070b55/1569358740365-XHKY3VFY8H86Q130T9MP/performers+on+stage?format=1000w"),
            Item("https://img.republicworld.com/republic-prod/stories/promolarge/xhdpi/ncc1qpyaopyyileo_1644234061.jpeg"),
            
        )
    }
}