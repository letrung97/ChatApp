package com.android.virtus.chatapp.Model

class Chat {
    var sender: String = ""
    var receiver: String = ""
    var message: String = ""
    var isseen: Boolean = false

    constructor() {}
    constructor(sender: String, receiver: String, message: String, isseen: Boolean) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isseen = isseen
    }
}