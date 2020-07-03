package com.fredrikbogg.snapchatclone.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.adapters.UserListAdapter
import com.fredrikbogg.snapchatclone.models.Snap
import com.fredrikbogg.snapchatclone.models.User
import com.fredrikbogg.snapchatclone.utils.*
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class SendSnapActivity : AppCompatActivity(), FirebaseDatabaseHelper.OnRetrieveUsersListener,
    FirebaseStorageHelper.OnSaveImageListener, FirebaseDatabaseHelper.OnRetrieveUserNameListener {
    private val progressDialogHandler: ProgressDialogHandler = ProgressDialogHandler()
    private var selectedUserUidList = ArrayList<String>()

    private lateinit var sender: String
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_snap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sendButton = findViewById(R.id.sendButton)
        loadUsers()
    }

    private fun loadUsers() {
        progressDialogHandler.toggleProgressDialog(this, true)
        FirebaseAuthHelper.getCurrentUser()?.uid?.let {
            FirebaseDatabaseHelper.getAllUsers(this, it)
        }
    }

    private fun setupUsersListView(userList: ArrayList<User>?) {
        val adapter = UserListAdapter(
            this,
            R.layout.list_item_selectable_user, userList
        )
        val userListView: ListView = findViewById(R.id.listView)

        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        userListView.adapter = adapter

        userListView.setOnItemClickListener { _, _, position, _ ->
            userList?.get(position)?.uid?.let {
                if (selectedUserUidList.contains(it)) {
                    selectedUserUidList.remove(it)
                } else {
                    selectedUserUidList.add(it)
                }
                toggleSendButtonEnabled()
            }
        }
    }

    private fun toggleSendButtonEnabled() {
        if (!sendButton.isEnabled && selectedUserUidList.size > 0) {
            sendButton.isEnabled = true
        } else if (sendButton.isEnabled && selectedUserUidList.size <= 0) {
            sendButton.isEnabled = false
        }
    }

    private fun startSendingSnap(byteArray: ByteArray) {
        for (x in 0 until selectedUserUidList.size) {
            FirebaseStorageHelper.savePhoto(this, byteArray)
        }
    }

    private fun endSendingSnap() {
        progressDialogHandler.toggleProgressDialog(this, false)
        Intent(this, CameraActivity::class.java).let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        }
    }

    private fun convertFileToByteArray(fileLocation: String): ByteArray {
        val bitmap = BitmapFactory.decodeStream(application.openFileInput(fileLocation))
        val byteArrayOutputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun sendButtonPressed(view: View) {
        val uid = FirebaseAuthHelper.getCurrentUser()?.uid
        if (uid != null) {
            progressDialogHandler.toggleProgressDialog(this, true)
            FirebaseDatabaseHelper.getNameFromUser(this, uid)
        } else {
            Toast.makeText(this, "Error sending snap", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onUsersRetrieved(success: Boolean, data: ArrayList<User>?) {
        progressDialogHandler.toggleProgressDialog(this, false)
        if (success && data != null) {
            if (data.size > 0) {
                setupUsersListView(data)
            } else {
                Toast.makeText(this, "No users", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show()
        }
    }

    @ExperimentalStdlibApi
    @Synchronized
    override fun onImageSaved(success: Boolean, path: String?) {
        if (success && path != null) {
            val snapToSend = Snap(path, System.currentTimeMillis().toString(), sender)
            FirebaseDatabaseHelper.addSnapToUser(selectedUserUidList.first(), snapToSend)
        }

        selectedUserUidList.removeFirst()

        if (selectedUserUidList.size <= 0) {
            endSendingSnap()
        }
    }

    override fun onUserNameRetrieved(success: Boolean, data: String?) {
        if (success && data != null) {
            val fileLocation = intent.getStringExtra(CameraCapturedActivity.EXTRA_FILE_LOCATION_ID)
            if (fileLocation != null) {
                sender = data
                val byteArray = convertFileToByteArray(fileLocation)
                startSendingSnap(byteArray)
                return
            }
        }
        progressDialogHandler.toggleProgressDialog(this, false)
        Toast.makeText(this, "Error sending snap", Toast.LENGTH_SHORT).show()
    }
}