package com.fredrikbogg.snapchatclone.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.utils.FirebaseAuthHelper
import com.fredrikbogg.snapchatclone.utils.FirebaseDatabaseHelper
import com.fredrikbogg.snapchatclone.utils.FirebaseStorageHelper
import com.fredrikbogg.snapchatclone.utils.ProgressDialogHandler

class ShowSnapActivity : AppCompatActivity(), FirebaseStorageHelper.OnDownloadImageListener {
    private val progressDialogHandler: ProgressDialogHandler = ProgressDialogHandler()
    private lateinit var imagePath: String
    private lateinit var snapId: String

    companion object {
        const val EXTRA_DB_IMAGE_ID = "image_id"
        const val EXTRA_DB_IMAGE_STORAGE_PATH_ID = "image_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_snap)
        supportActionBar?.hide()
        loadSnap()
    }

    fun onScreenPressed(view: View) {
        finish()
    }

    private fun startTimer() {
        val timeInSeconds = 8
        var currentTime = timeInSeconds

        val countdownTextView = findViewById<TextView>(R.id.countdownTextView)
        countdownTextView.text = (currentTime.toString())

        object : CountDownTimer((timeInSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime -= 1
                countdownTextView.text = currentTime.toString()
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    private fun loadSnap() {
        val path = intent.getStringExtra(EXTRA_DB_IMAGE_STORAGE_PATH_ID)
        val id = intent.getStringExtra(EXTRA_DB_IMAGE_ID)
        if (path != null && id != null) {
            progressDialogHandler.toggleProgressDialog(this, true)
            snapId = id
            imagePath = path

            FirebaseStorageHelper.downloadPhoto(this, imagePath)
        }
    }

    override fun onImageDownloaded(success: Boolean, byteArray: ByteArray?) {
        progressDialogHandler.toggleProgressDialog(this, false)
        if (success && byteArray != null) {
            val bitmap = byteArray.size.let { BitmapFactory.decodeByteArray(byteArray, 0, it) }
            findViewById<ImageView>(R.id.snapImageView).setImageBitmap(bitmap)

            val userUid = FirebaseAuthHelper.getCurrentUser()?.uid

            if (userUid != null) {
                FirebaseDatabaseHelper.deleteSnapFromUser(userUid, snapId)
                FirebaseStorageHelper.deleteFromStorage(imagePath)
            }
            startTimer()

        } else {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }
}