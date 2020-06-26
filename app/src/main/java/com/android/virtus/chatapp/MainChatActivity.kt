package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.virtus.chatapp.Fragments.ChatsFragment
import com.android.virtus.chatapp.Fragments.ProfileFragment
import com.android.virtus.chatapp.Fragments.UserFragment
import com.android.virtus.chatapp.Model.User
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MainChatActivity : AppCompatActivity() {
    lateinit var profile_image: CircleImageView
    var userName: TextView? = null
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        profile_image = findViewById(R.id.profile_image)
        userName = findViewById(R.id.username)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                userName?.setText(user!!.name)
                if (user!!.imageURL == "default") {
                    profile_image.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user.imageURL).into(profile_image)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(), "Tin nhắn")
        viewPagerAdapter.addFragment(UserFragment(), "Bạn bè")
        viewPagerAdapter.addFragment(ProfileFragment(), "Tài khoản")
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainChatActivity, DangNhapActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                return true
            }
        }
        return false
    }

    internal inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

    }

    private fun updateStatus(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference?.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }
}