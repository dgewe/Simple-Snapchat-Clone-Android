package com.fredrikbogg.snapchatclone.utils

import com.fredrikbogg.snapchatclone.models.Snap
import com.fredrikbogg.snapchatclone.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseHelper {
    private const val PATH_USERS = "/users"
    private const val PATH_USER_SNAPS = "/snaps"
    private const val PATH_USER_NAME = "/name"

    interface OnRetrieveUsersListener {
        fun onUsersRetrieved(success: Boolean, data: ArrayList<User>? = null)
    }

    interface OnRetrieveUserSnapsListener {
        fun onUserSnapsRetrieved(success: Boolean, data: ArrayList<Snap>? = null)
    }

    interface OnRetrieveUserNameListener {
        fun onUserNameRetrieved(success: Boolean, data: String? = null)
    }

    fun addSnapToUser(uid: String, snap: Snap) {
        FirebaseDatabase.getInstance().reference.child("$PATH_USERS/$uid$PATH_USER_SNAPS").push()
            .setValue(snap)
    }

    fun addUser(user: User) {
        FirebaseDatabase.getInstance().getReference("$PATH_USERS/${user.uid}").setValue(user)
    }

    fun getAllUsers(listener: OnRetrieveUsersListener, ignoreUid: String) {
        val myRef = FirebaseDatabase.getInstance().reference.child(PATH_USERS)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (data in dataSnapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null && !user.uid.equals(ignoreUid)) {
                        userList.add(user)
                    }
                }
                listener.onUsersRetrieved(true, userList)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onUsersRetrieved(false)
            }
        })
    }

    fun getSnapsFromUser(listener: OnRetrieveUserSnapsListener, uid: String) {
        val myRef =
            FirebaseDatabase.getInstance().reference.child("$PATH_USERS/$uid$PATH_USER_SNAPS")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapList = ArrayList<Snap>()

                for (data in dataSnapshot.children) {
                    val snap = data.getValue(Snap::class.java)
                    if (snap != null) {
                        snap.id = data.key
                        snapList.add(snap)
                    }
                }

                listener.onUserSnapsRetrieved(true, snapList)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onUserSnapsRetrieved(false)
            }
        })
    }

    fun getNameFromUser(listener: OnRetrieveUserNameListener, uid: String) {
        val myRef =
            FirebaseDatabase.getInstance().reference.child("$PATH_USERS/$uid$PATH_USER_NAME")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.getValue(String::class.java)
                listener.onUserNameRetrieved(true, name)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onUserNameRetrieved(false)
            }
        })
    }

    fun deleteSnapFromUser(uid: String, snapId: String) {
        FirebaseDatabase.getInstance().reference.child("$PATH_USERS/$uid$PATH_USER_SNAPS/$snapId")
            .setValue(null)
    }
}