package com.example.plantbuddy.core

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.plantbuddy.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object UserCore {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = null

    init {
        user = auth.currentUser
    }

    fun checkIfLoggedIn():Boolean {
        return user != null;
    }

    fun signOut()
    {
        auth.signOut()
    }
}