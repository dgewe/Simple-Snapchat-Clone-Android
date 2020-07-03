package com.fredrikbogg.snapchatclone.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.fredrikbogg.snapchatclone.R
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class CameraFragment : Fragment(), SurfaceHolder.Callback {
    private lateinit var onPhotoCapturedListener: OnPhotoCapturedListener
    private lateinit var camera: Camera
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var jpegCallback: PictureCallback

    private lateinit var captureImageBtn: ImageButton
    private lateinit var flashBtn: ImageButton
    private lateinit var rotateCameraBtn: ImageButton

    private var layoutView: View? = null

    private val cameraRequestCode = 1

    private var isFlashOn = false
    private var isCameraFacingFront = false

    interface OnPhotoCapturedListener {
        fun onPhotoCaptured(fileLocation: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        surfaceView = view.findViewById(R.id.surfaceView)
        surfaceHolder = surfaceView.holder

        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        val permission = context?.let { checkSelfPermission(it, Manifest.permission.CAMERA) }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            surfaceView.visibility = View.GONE
            view.visibility = View.GONE
            layoutView = view
            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
        }

        flashBtn = view.findViewById(R.id.toggleCameraFlashButton)
        rotateCameraBtn = view.findViewById(R.id.rotateCameraButton)
        captureImageBtn = view.findViewById(R.id.captureImageButton)

        flashBtn.setOnClickListener {
            toggleCameraFlashPressed()
        }

        rotateCameraBtn.setOnClickListener {
            rotateCameraPressed()
        }

        captureImageBtn.setOnClickListener {
            captureImagePressed()
        }

        jpegCallback = PictureCallback { data, _ ->
            val decodedBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val rotatedBitmap: Bitmap = rotateBitmap(decodedBitmap)
            val fileLocation = saveImageToStorage(rotatedBitmap)

            if (fileLocation != null) {
                onPhotoCapturedListener.onPhotoCaptured(fileLocation)
            } else {
                throw Exception("Error saving image to storage")
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onPhotoCapturedListener = context as OnPhotoCapturedListener
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camera.apply {
            stopPreview()
            release()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        updateFlashModeImage(isFlashOn)
        setupCamera()
    }

    private fun toggleCameraFlashPressed() {
        isFlashOn = !isFlashOn
        updateFlashModeImage(isFlashOn)

        val parameters = camera.parameters
        parameters.flashMode = getFlashMode(isFlashOn)
        camera.parameters = parameters
    }

    private fun rotateCameraPressed() {
        isCameraFacingFront = !isCameraFacingFront
        setupCamera()
    }

    private fun captureImagePressed() {
        camera.takePicture(null, null, jpegCallback)
    }

    private fun getFlashMode(isOn: Boolean): String {
        return if (isOn && !isCameraFacingFront) Camera.Parameters.FLASH_MODE_ON else Camera.Parameters.FLASH_MODE_OFF
    }

    private fun getCameraRotation(isFacingFront: Boolean): Int {
        return if (isFacingFront) Camera.CameraInfo.CAMERA_FACING_FRONT else Camera.CameraInfo.CAMERA_FACING_BACK
    }

    private fun updateFlashModeImage(isOn: Boolean) {
        val img = if (isOn) R.drawable.flash_on else R.drawable.flash_off
        flashBtn.setImageResource(img)
    }

    private fun setupCameraParameters(camera: Camera): Camera.Parameters {
        val parameters = camera.parameters
        val sizeList = camera.parameters.supportedPreviewSizes

        var bestSize = sizeList[0]
        for (i in 1 until sizeList.size) {
            if (bestSize != null && (sizeList[i].width * sizeList[i].height > bestSize.width * bestSize.height)) {
                bestSize = sizeList[i]
            }
        }

        parameters.apply {
            previewFrameRate = 30
            setPictureSize(bestSize!!.width, bestSize.height)
            flashMode = getFlashMode(isFlashOn)
            if (!isCameraFacingFront) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            }
        }

        return parameters
    }

    private fun setupCamera() {
        camera = Camera.open(getCameraRotation(isCameraFacingFront))
        camera.setDisplayOrientation(90)
        camera.parameters = setupCameraParameters(camera)
        camera.setPreviewDisplay(surfaceHolder)
        camera.startPreview()
    }

    private fun rotateBitmap(decodedBitmap: Bitmap): Bitmap {
        val w = decodedBitmap.width
        val h = decodedBitmap.height
        val matrix = Matrix()

        if (isCameraFacingFront) {
            matrix.setRotate(-90F)
            matrix.preScale(1.0f, -1.0f)
        } else {
            matrix.setRotate(90F)
        }
        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true)
    }

    private fun saveImageToStorage(bitmap: Bitmap): String? {
        val fileName = "image_to_send"
        return try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val fo: FileOutputStream = context!!.openFileOutput(fileName, Context.MODE_PRIVATE)
            fo.write(bytes.toByteArray())
            fo.close()
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequestCode) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    context,
                    "Permission is needed to use the camera",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                surfaceView.visibility = View.VISIBLE
                layoutView?.visibility = View.VISIBLE
            }
        }
    }
}