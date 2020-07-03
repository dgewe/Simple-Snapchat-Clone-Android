package com.fredrikbogg.snapchatclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.models.Snap
import com.fredrikbogg.snapchatclone.utils.TimeAndDateConverter

class UserSnapsListAdapter(
    context: Context,
    resource: Int,
    objects: ArrayList<Snap>?
) :
    ArrayAdapter<Snap?>(context, resource, objects!! as List<Snap?>) {
    private val mContext: Context = context
    private val mResource: Int = resource
    private var lastPosition = -1

    private class ViewHolder {
        var senderTextView: TextView? = null
        var timeTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cView = convertView
        val snap = getItem(position) as Snap

        val holder: ViewHolder
        if (cView == null) {
            cView = LayoutInflater.from(mContext).inflate(mResource, parent, false)
            holder = ViewHolder()
            holder.senderTextView = cView.findViewById<View>(R.id.senderTextView) as TextView
            holder.timeTextView = cView.findViewById<View>(R.id.timeTextView) as TextView
            cView.tag = holder
        } else {
            holder = cView.tag as ViewHolder
        }
        lastPosition = position

        holder.senderTextView!!.text = snap.sender
        holder.timeTextView!!.text = snap.time?.toLong()?.let {
            TimeAndDateConverter.getTimeAndDateFromEpoch(it)
        }

        return cView!!
    }
}