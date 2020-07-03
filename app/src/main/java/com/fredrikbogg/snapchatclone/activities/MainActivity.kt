package com.fredrikbogg.snapchatclone.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.models.User
import com.fredrikbogg.snapchatclone.models.UserLogin
import com.fredrikbogg.snapchatclone.models.UserRegister
import com.fredrikbogg.snapchatclone.fragments.LoginFragment
import com.fredrikbogg.snapchatclone.fragments.RegisterFragment
import com.fredrikbogg.snapchatclone.utils.ProgressDialogHandler
import com.fredrikbogg.snapchatclone.utils.FirebaseAuthHelper
import com.fredrikbogg.snapchatclone.utils.FirebaseDatabaseHelper
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), LoginFragment.OnLoginListener,
    RegisterFragment.OnRegisterListener, FirebaseAuthHelper.OnLoginListener,
    FirebaseAuthHelper.OnRegistrationListener {

    private val logInFragment: LoginFragment = LoginFragment()
    private val registerFragment: Fragment = RegisterFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val progressDialogHandler: ProgressDialogHandler = ProgressDialogHandler()

    private lateinit var userRegister: UserRegister

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(logInFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, fragment)
            commit()
        }
    }

    fun switchFragment(view: View) {
        val checked = (view as RadioButton).isChecked
        if (checked) {
            when (view.id) {
                R.id.loginRadioButton -> replaceFragment(logInFragment)
                R.id.registerRadioButton -> replaceFragment(registerFragment)
                else -> throw Exception("Error switching view")
            }
        }
    }

    override fun onLogin(userLogin: UserLogin) {
        if (isFieldsValid(userLogin)) {
            progressDialogHandler.toggleProgressDialog(this, true)
            FirebaseAuthHelper.signIn(this, userLogin.email, userLogin.password)
        } else {
            Toast.makeText(this, "Error Signing In", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRegister(userRegister: UserRegister) {
        if (isFieldsValid(userRegister)) {
            this.userRegister = userRegister
            progressDialogHandler.toggleProgressDialog(this, true)
            FirebaseAuthHelper.register(this, this.userRegister.email, this.userRegister.password)
        } else {
            onRegisterFinished(false, null)
        }
    }

    private fun isFieldsValid(userRegister: UserRegister): Boolean {
        return (userRegister.name.isNotEmpty() && userRegister.email.isNotEmpty()
                && userRegister.password.isNotEmpty() && userRegister.confirmPassword.isNotEmpty() && userRegister.password == userRegister.confirmPassword)
    }

    private fun isFieldsValid(userLogin: UserLogin): Boolean {
        return (userLogin.email.isNotEmpty() && userLogin.password.isNotEmpty())
    }

    private fun goToCameraActivity() {
        if (FirebaseAuthHelper.getCurrentUser() != null) {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    override fun onSignInFinished(success: Boolean) {
        progressDialogHandler.toggleProgressDialog(this, false)
        if (success) {
            goToCameraActivity()
        } else {
            Toast.makeText(this, "Error Signing In", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRegisterFinished(success: Boolean, user: FirebaseUser?) {
        progressDialogHandler.toggleProgressDialog(this, false)
        if (success) {
            if (user != null) {
                FirebaseDatabaseHelper.addUser(User(user.uid, userRegister.name))
            }
            goToCameraActivity()

        } else {
            Toast.makeText(this, "Error Registering", Toast.LENGTH_SHORT).show()
        }
    }
}