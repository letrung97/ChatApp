package com.android.virtus.chatapp.Model

class ChatList {
    var id: String = ""

    constructor() {}
    constructor(id: String?) {
        if (id != null) {
            this.id = id
        }
    }
}