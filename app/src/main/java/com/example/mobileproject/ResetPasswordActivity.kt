package com.example.mobileproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password )

        val resetBtn =findViewById<Button>(R.id.ResetBtn)

        resetBtn.setOnClickListener(View.OnClickListener {
        var email: EditText = findViewById(R.id.emailET)
        var emailText:String = email.text.toString()
        resetPassword(emailText)
        })
    }
    private fun resetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this@ResetPasswordActivity, "Please check your email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                finish();
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@ResetPasswordActivity, "Error sending email: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}