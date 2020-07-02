package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.virtus.chatapp.Adapter.MessageAdapter
import com.android.virtus.chatapp.Model.Chat
import com.android.virtus.chatapp.Model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageActivity : AppCompatActivity() {
    lateinit var profile_image: CircleImageView
    var username: TextView? = null
    var fuser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var btn_send: ImageButton? = null
    var text_send: EditText? = null
    var messageAdapter: MessageAdapter? = null
    var mChat: MutableList<Chat?>? = null
    var recyclerView: RecyclerView? = null
    var seenListener: ValueEventListener? = null
    var userid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { startActivity(Intent(this@MessageActivity, MainChatActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView?.setLayoutManager(linearLayoutManager)
        profile_image = findViewById(R.id.profile_image)
        username = findViewById(R.id.username)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)
        intent = getIntent()
        userid = intent.getStringExtra("userid")
        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid)
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                username?.setText(user!!.name)
                if (user!!.imageURL == "default") {
                    profile_image.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user.imageURL).into(profile_image)
                }
                readMessage(fuser!!.uid, userid, user.imageURL)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        btn_send?.setOnClickListener {
            val msg = text_send?.getText().toString().trim { it <= ' ' }
            if (!msg.isEmpty()) {
                SendMessage(fuser!!.uid, userid, msg)
            } else {
                Toast.makeText(this@MessageActivity, "Bạn chưa nhập nội dung để gửi!", Toast.LENGTH_SHORT).show()
            }
            text_send?.setText("")
        }
        seenMessage(userid)
    }

    private fun seenMessage(userid: String?) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    //nếu người nhận là user đang đăng nhập và người gửi là ng mà user đang chat
                    if (chat!!.receiver == fuser!!.uid && chat.sender == userid) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun SendMessage(sender: String, receiver: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any?>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false
        reference.child("Chats").push().setValue(hashMap)
        val chatRef1 = userid.let { FirebaseDatabase.getInstance().getReference("ChatList").child(fuser!!.uid).child(it) }
        chatRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(userid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val chatRef = userid.let { FirebaseDatabase.getInstance().getReference("ChatList").child(it).child(fuser!!.uid) }
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(fuser!!.uid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readMessage(myid: String, userid: String?, imageurl: String?) {
        mChat = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mChat as ArrayList<Chat?>).clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == myid && chat.sender == userid || chat.receiver == userid && chat.sender == myid) {
                        (mChat as ArrayList<Chat?>).add(chat)
                    }
                    messageAdapter = MessageAdapter(this@MessageActivity, mChat as ArrayList<Chat?>, imageurl!!)
                    recyclerView!!.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun updateStatus(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        seenListener?.let { reference!!.removeEventListener(it) }
        updateStatus("offline")
    }
}