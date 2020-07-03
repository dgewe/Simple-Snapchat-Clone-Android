package com.fredrikbogg.snapchatclone.models

import com.fredrikbogg.snapchatclone.utils.SerializedName

class User() {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("uid")
    var uid: String? = null

    constructor(uid: String, name: String) : this() {
        this.uid = uid
        this.name = name
    }
}