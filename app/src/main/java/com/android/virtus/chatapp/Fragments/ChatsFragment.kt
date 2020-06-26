package com.android.virtus.chatapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.virtus.chatapp.Adapter.UserAdapter
import com.android.virtus.chatapp.Model.ChatList
import com.android.virtus.chatapp.Model.User
import com.android.virtus.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class ChatsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var userAdapter: UserAdapter? = null
    var mUser: MutableList<User?>? = null
    var fuser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private lateinit var usersList: MutableList<ChatList?>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(context))
        fuser = FirebaseAuth.getInstance().currentUser!!
        usersList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser!!.uid)
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (usersList as ArrayList<ChatList?>).clear()
                for (snapshot in dataSnapshot.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)
                    (usersList as ArrayList<ChatList?>).add(chatList)
                }
                chatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return view
    }

    private fun chatList() {
        mUser = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUser as ArrayList<User?>).clear()
                //duyệt qua các user trong bảng user
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    //duyệt qua các user khác user này đã nhắn, lưu trong chatlist
                    for (chatList in usersList) {
                        if (user != null) {
                            if (chatList != null) {
                                if (user.id.equals(chatList.id)) {
                                    (mUser as ArrayList<User?>).add(user)
                                }
                            }
                        }
                    }
                }
                userAdapter = UserAdapter(context, mUser, true)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}