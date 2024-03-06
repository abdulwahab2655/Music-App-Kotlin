package com.example.mobileproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val LoginBtn=findViewById<Button>(R.id.LoginBtn)
        val SignupBtn=findViewById<Button>(R.id.SignupBtn)

        LoginBtn.setOnClickListener {
            // Start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish();
        }
        SignupBtn.setOnClickListener(View.OnClickListener {
            var email: EditText = findViewById(R.id.emailET)
            var password: EditText = findViewById(R.id.passwordET)
            var emailText:String = email.text.toString()
            var passwordText:String = password.text.toString()
            registerUser(emailText, passwordText)
        })
    }

    private fun registerUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(this@SignUpActivity, "Please verify your email", Toast.LENGTH_SHORT).show()
                                Log.d("Registration", "${user.email} successfully Registered.")
//                                Toast.makeText(this@SignUpActivity, "Registration successful.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            } else {
                                Toast.makeText(this@SignUpActivity, "Error verifying email", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e("Registration", "Registration failure", task.exception)
                    Toast.makeText(this@SignUpActivity, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


}