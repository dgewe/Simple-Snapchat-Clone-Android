package com.fredrikbogg.snapchatclone.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.fredrikbogg.snapchatclone.R
import java.io.FileNotFoundException

class CameraCapturedActivity : AppCompatActivity() {

    private var fileLocation: String? = null

    companion object {
        const val EXTRA_FILE_LOCATION_ID = "file_location"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_captured)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fileLocation = intent.getStringExtra(EXTRA_FILE_LOCATION_ID)
        setCapturedImage()
    }

    private fun setCapturedImage() {
        try {
            val bitmap = fileLocation?.let { convertFileToBitmap(it) }
            findViewById<ImageView>(R.id.captureImage).setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            finish()
        }
    }

    private fun convertFileToBitmap(fileLocation: String): Bitmap {
        return BitmapFactory.decodeStream(application.openFileInput(fileLocation))
    }

    fun sendImagePressed(view: View) {
        Intent(this, SendSnapActivity::class.java).let {
            it.putExtra(EXTRA_FILE_LOCATION_ID, fileLocation)
            startActivity(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}