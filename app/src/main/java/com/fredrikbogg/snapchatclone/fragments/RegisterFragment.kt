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
import com.fredrikbogg.snapchatclone.models.UserRegister

class RegisterFragment : Fragment() {
    private lateinit var onRegisterListener: OnRegisterListener

    interface OnRegisterListener {
        fun onRegister(userRegister: UserRegister)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)
        val emailAddressEditText: EditText = view.findViewById(R.id.emailAddressEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = view.findViewById(R.id.confirmPasswordEditText)

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            onRegisterListener.onRegister(
                UserRegister(
                    nameEditText.text.toString(),
                    emailAddressEditText.text.toString(),
                    passwordEditText.text.toString(),
                    confirmPasswordEditText.text.toString()
                )
            )
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRegisterListener = context as OnRegisterListener
    }
}