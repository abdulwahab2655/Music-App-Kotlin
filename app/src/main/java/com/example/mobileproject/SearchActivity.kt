package com.example.mobileproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEt=findViewById<EditText>(R.id.searchET)
        val searchBtn=findViewById<Button>(R.id.SearchBtn)
        val favouriteBtn=findViewById<Button>(R.id.favouriteBtn)

        searchBtn.setOnClickListener{
            var searchText=searchEt.text.toString()
            if (searchText.isNotEmpty()) {
                val intent = Intent(this@SearchActivity, PlayListActivity::class.java)
                intent.putExtra("Artist", searchText)
                startActivity(intent)
            } else {
                Toast.makeText(this@SearchActivity, "Oops! Search field is empty", Toast.LENGTH_SHORT).show()
            }

        }
        favouriteBtn.setOnClickListener{
                val intent = Intent(this@SearchActivity, FavouriteActivity::class.java)
                startActivity(intent)
            }
        }
    }