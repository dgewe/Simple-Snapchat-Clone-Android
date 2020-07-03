package com.fredrikbogg.snapchatclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fredrikbogg.snapchatclone.R
import com.fredrikbogg.snapchatclone.models.User

class UserListAdapter(context: Context, resource: Int, objects: ArrayList<User>?) :
    ArrayAdapter<User?>(context, resource, objects!! as List<User?>) {
    private val mContext: Context = context
    private val mResource: Int = resource
    private var lastPosition = -1

    private class ViewHolder {
        var nameTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cView = convertView
        val name = (getItem(position) as User).name
        val holder: ViewHolder

        if (cView == null) {
            cView = LayoutInflater.from(mContext).inflate(mResource, parent, false)
            holder = ViewHolder()
            holder.nameTextView = cView.findViewById<View>(R.id.nameTextView) as TextView
            cView.tag = holder
        } else {
            holder = cView.tag as ViewHolder
        }
        lastPosition = position
        holder.nameTextView!!.text = name

        return cView!!
    }
}