package com.example.plantbuddy

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signup_button: Button
    private lateinit var email_text: EditText
    private lateinit var password_text: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        signup_button = findViewById(R.id.buttonSignUp)
        email_text = findViewById(R.id.edit_email)
        password_text = findViewById(R.id.edit_password)

        signup_button.setOnClickListener{
            createAccount(email_text.getText().toString().trim(), password_text.getText().toString().trim())

        }
        setToolbar()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = auth.getCurrentUser()
    }

    private fun setToolbar()
    {
        val backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        val title = findViewById<TextView>(R.id.tv_toolbar_title)
        title.setText(R.string.register)
    }

    private fun createAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@SignupActivity, "Create account failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }
}