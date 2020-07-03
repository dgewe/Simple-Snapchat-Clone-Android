package com.fredrikbogg.snapchatclone.utils

import android.app.ProgressDialog
import android.content.Context
import android.widget.ProgressBar
import com.fredrikbogg.snapchatclone.R

class ProgressDialogHandler {
    private var progressDialog: ProgressDialog? = null

    fun toggleProgressDialog(context: Context, active: Boolean) {
        if (active) {
            progressDialog = ProgressDialog(context, R.style.CustomProgressDialogTheme).apply {
                setCancelable(false)
                show()
                setContentView(ProgressBar(context))
            }
        } else {
            progressDialog?.dismiss()
        }
    }
}