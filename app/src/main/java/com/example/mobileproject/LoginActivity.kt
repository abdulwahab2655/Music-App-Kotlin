package com.example.mobileproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var LoginBtn: Button
    private lateinit var SignupBtn: Button
    private lateinit var ForgetBtn: Button

    companion object{
        const val preferenceNameText: String = "CREDENTIALS"
        const val emailText: String = "EMAIL"
        const val passwordText: String = "PASSWORD"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginBtn=findViewById(R.id.LoginBtn)
        SignupBtn=findViewById(R.id.SignupBtn)
        ForgetBtn=findViewById(R.id.forgetPasswordBtn)

        retrieveCredentialsAndSetData()
        LoginBtn.setOnClickListener(View.OnClickListener {
            emailET = findViewById(R.id.emailET)
            passwordET = findViewById(R.id.passwordET)
            var emailText:String = emailET.text.toString()
            var passwordText:String = passwordET.text.toString()
            val rememberCheckBox: CheckBox = findViewById(R.id.checkbox)
            storeCredentials(emailText, passwordText)
            authenticateUser(emailText, passwordText)
        })
        SignupBtn.setOnClickListener {
            // Start SignupActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish();
        }
        ForgetBtn.setOnClickListener{
            // Start SignupActivity
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish();
        }
    }
    private fun storeCredentials(email:String, password:String ):Unit {
        val sharedPreference = getSharedPreferences(preferenceNameText,Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(emailText, email)
        editor.putString(passwordText, password)
        editor.apply()
        Log.d("MainActivity", "Stored credentials: Username - $email, Password - $password")

    }
    private fun retrieveCredentialsAndSetData(){
        val sharedPreference = getSharedPreferences(preferenceNameText,
            Context.MODE_PRIVATE)
        val username = sharedPreference.getString(emailText, "")
        val password = sharedPreference.getString(passwordText, "")

        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        if (!username.equals("") && !password.equals("")) {
            emailET.setText(username)
            passwordET.setText(password)
        }
    }
    private fun authenticateUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isEmailVerified = auth.currentUser?.isEmailVerified == true

                    if (isEmailVerified) {
                        val user = auth.currentUser
                        Log.d("Login", "${user?.email} successfully login.")
                        Toast.makeText(this@LoginActivity, "Authentication successful.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SearchActivity::class.java))
                    } else {
                        Toast.makeText(this@LoginActivity, "Please verify your email", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Login", "Authentication failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
