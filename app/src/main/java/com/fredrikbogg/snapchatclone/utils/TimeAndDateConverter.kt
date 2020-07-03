package com.fredrikbogg.snapchatclone.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeAndDateConverter {

    // example of result: 6/17 11 AM
    @SuppressLint("SimpleDateFormat")
    fun getTimeAndDateFromEpoch(epochSeconds: Long): String? {
        val pattern =
            SimpleDateFormat().toLocalizedPattern().replace("\\W?[Yy]+\\W?".toRegex(), " ")

        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(epochSeconds))
    }
}