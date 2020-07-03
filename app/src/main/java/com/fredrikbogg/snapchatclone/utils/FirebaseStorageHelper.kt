package com.fredrikbogg.snapchatclone.utils

import com.google.firebase.storage.FirebaseStorage
import java.util.*

object FirebaseStorageHelper {

    interface OnSaveImageListener {
        fun onImageSaved(success: Boolean, path: String? = null)
    }

    interface OnDownloadImageListener {
        fun onImageDownloaded(success: Boolean, byteArray: ByteArray? = null)
    }

    fun savePhoto(onSaveImageListener: OnSaveImageListener, byteArray: ByteArray) {
        val storageRef = FirebaseStorage.getInstance().reference
        val path = UUID.randomUUID().toString()
        val uploadTask = storageRef.child(path).putBytes(byteArray)

        uploadTask.addOnFailureListener {
            onSaveImageListener.onImageSaved(false)
        }.addOnSuccessListener {
            onSaveImageListener.onImageSaved(true, path)
        }
    }

    fun downloadPhoto(onDownloadImageListener: OnDownloadImageListener, path: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val islandRef = storageRef.child(path)

        val size: Long = 1024 * 1024
        islandRef.getBytes(size).addOnSuccessListener {
            onDownloadImageListener.onImageDownloaded(true, it)
        }.addOnFailureListener {
            onDownloadImageListener.onImageDownloaded(false)
        }
    }

    fun deleteFromStorage(path: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val desertRef = storageRef.child(path)
        desertRef.delete()
    }
}