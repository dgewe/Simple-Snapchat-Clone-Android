package com.fredrikbogg.snapchatclone.utils

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthHelper {

    interface OnLoginListener {
        fun onSignInFinished(success: Boolean)
    }

    interface OnRegistrationListener {
        fun onRegisterFinished(success: Boolean, user: FirebaseUser? = null)
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun signIn(listener: OnLoginListener, email: String, password: String) {
        val activity = listener as Activity
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task -> listener.onSignInFinished(task.isSuccessful) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun register(listener: OnRegistrationListener, email: String, password: String) {
        val activity = listener as Activity

        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    activity
                ) { task ->
                    val user = FirebaseAuth.getInstance().currentUser
                    if (task.isSuccessful && user != null) {
                        listener.onRegisterFinished(true, user)
                    } else {
                        listener.onRegisterFinished(false)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun signOut(): Boolean {
        FirebaseAuth.getInstance().signOut()
        return FirebaseAuth.getInstance().currentUser == null
    }
}