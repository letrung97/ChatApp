package com.android.virtus.chatapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.virtus.chatapp.Adapter.UserAdapter
import com.android.virtus.chatapp.Model.User
import com.android.virtus.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserFragment : Fragment() {
    var recyclerView: RecyclerView?= null
    var userAdapter: UserAdapter?= null
    var mUsers: MutableList<User?>?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(context))
        mUsers = ArrayList()
        readUser()
        return view
    }

    private fun readUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!
                    assert(firebaseUser != null)
                    if (!user.id.equals(firebaseUser?.uid)) {
                        mUsers!!.add(user)
                    }
                }
                userAdapter = UserAdapter(context, mUsers, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}