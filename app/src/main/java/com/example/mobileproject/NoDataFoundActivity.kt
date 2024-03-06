package com.example.mobileproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NoDataFoundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_data_found)

        val backBtn=findViewById<Button>(R.id.BackBtn)
        backBtn.setOnClickListener{
            val intent = Intent(this@NoDataFoundActivity, SearchActivity::class.java)
            startActivity(intent)
            finish();
        }
    }
}