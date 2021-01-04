package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.plantbuddy.helpers.showErrorSnackbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var singUpButton:Button
    lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var email_text: EditText
    private lateinit var password_text: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeViews()

        email_text.setText("andrada@gmail.com")
        password_text.setText("andrada")

        singUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{
            if(email_text.text.isEmpty() || password_text.text.isEmpty())
            {
                showErrorSnackbar(findViewById(R.id.coordinatorLayout), "Username and password cannot be empty")
                return@setOnClickListener;
            }
            loginUser(email_text.getText().toString().trim(), password_text.getText().toString().trim())
        }
    }

    fun loginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "login:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        showErrorSnackbar(findViewById(R.id.coordinatorLayout), "Username and password incorrect")
                    }
                })
    }

    private fun initializeViews()
    {
        auth = FirebaseAuth.getInstance()
        singUpButton = findViewById(R.id.btn_sign_up)
        loginButton = findViewById(R.id.btn_log_in)
        email_text = findViewById(R.id.tv_username)
        password_text = findViewById(R.id.tv_password)
    }
}