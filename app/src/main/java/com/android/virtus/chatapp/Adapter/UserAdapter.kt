package com.android.virtus.chatapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.virtus.chatapp.MessageActivity
import com.android.virtus.chatapp.Model.Chat
import com.android.virtus.chatapp.Model.User
import com.android.virtus.chatapp.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserAdapter(private val mContext: Context?, private val mUser: MutableList<User?>?, //người có tin nhắn
                  private val isChat: Boolean) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var lastMessage: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = this.mUser?.get(position)
        holder.username.setText(user!!.name)
        if (user.imageURL.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext!!).load(user.imageURL).into(holder.profile_image)
        }
        if (isChat) {
            user.id.let { showLastMessage(it, holder.last_msg) }
        } else {
            holder.last_msg.visibility = View.GONE
        }
        if (isChat) {
            if (user.status.equals("online")) {
                holder.img_on.visibility = View.VISIBLE
                holder.img_off.visibility = View.GONE
            } else {
                holder.img_on.visibility = View.GONE
                holder.img_off.visibility = View.VISIBLE
            }
        } else {
            holder.img_on.visibility = View.GONE
            holder.img_off.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MessageActivity::class.java)
            intent.putExtra("userid", user.id)
            mContext!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mUser!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var profile_image: ImageView
        val img_on: ImageView
        val img_off: ImageView
        val last_msg: TextView

        init {
            username = itemView.findViewById(R.id.username)
            profile_image = itemView.findViewById(R.id.profile_image)
            img_on = itemView.findViewById(R.id.img_on)
            img_off = itemView.findViewById(R.id.img_off)
            last_msg = itemView.findViewById(R.id.last_msg)
        }
    }

    private fun showLastMessage(userid: String, last_msg: TextView) {
        lastMessage = "default"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(firebaseUser?.uid) && chat?.sender.equals(userid)
                            || chat?.sender.equals(firebaseUser?.uid) && chat?.receiver.equals(userid)) {
                        lastMessage = chat?.message
                    }
                }
                if ("default" == lastMessage) {
                    last_msg.text = "Không có tin nhắn"
                } else {
                    last_msg.text = lastMessage
                }
                lastMessage = "default"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}