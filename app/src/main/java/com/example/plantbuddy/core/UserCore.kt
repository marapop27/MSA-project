package com.example.plantbuddy.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object UserCore {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = null

    init {
        user = auth.currentUser
    }

    fun checkIfLoggedIn():Boolean {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        return user != null
    }

    fun signOut()
    {
        auth.signOut()
    }
}