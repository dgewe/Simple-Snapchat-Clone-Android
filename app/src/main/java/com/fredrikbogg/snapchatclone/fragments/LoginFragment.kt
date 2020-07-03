package com.fredrikbogg.snapchatclone.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.models.UserLogin

class LoginFragment : Fragment() {
    private lateinit var onLoginListener: OnLoginListener

    interface OnLoginListener {
        fun onLogin(userLogin: UserLogin)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val emailAddressEditText: EditText = view.findViewById(R.id.emailAddressEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)

        view.findViewById<Button>(R.id.logInButton).setOnClickListener {
            onLoginListener.onLogin(
                UserLogin(emailAddressEditText.text.toString(), passwordEditText.text.toString())
            )
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onLoginListener = context as OnLoginListener
    }
}