package com.android.virtus.chatapp.Model

class User {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var imageURL: String = ""
    var status: String = ""

    constructor() {}
    constructor(id: String, name: String, email: String, imageURL: String, status: String) {
        this.id = id
        this.name = name
        this.email = email
        this.status = status
        this.imageURL = imageURL
    }
}