package com.android.virtus.chatapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.virtus.chatapp.Model.Chat
import com.android.virtus.chatapp.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MessageAdapter(private val mContext: Context, private val mChat: MutableList<Chat?>, private val imageurl: String) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val lastPosition = -1
    var fuser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        holder.show_message.setText(chat!!.message)
        if (imageurl == "default") {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image)
        }
        if (position == mChat.size - 1) { //kiểm tra lấy ra tin nhắn cuối cùng
            if (chat.isseen) {
                holder.txt_seen.text = "Đã xem"
            } else {
                holder.txt_seen.text = "Đã nhận"
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var profile_image: ImageView
        var txt_seen: TextView

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position]!!.sender.equals(fuser!!.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

}