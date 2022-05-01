package com.machine.dailytodo.ui.dashboard.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class TodoModel(

    val todoId : String,
    val mobile: String,
    val todoDate: String,
    val priority: String,
    val todoTime: String,
    val key: String,
    val todoDesc: String,
    val todoName: String,
    val createdDate: String,
    val status: String,
    val comments: String,
    val createdTime: String,@get:Exclude var id: String = "") {
    constructor():this("","","","","","","","","","","","")
}