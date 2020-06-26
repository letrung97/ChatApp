package com.android.virtus.chatapp.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.virtus.chatapp.Model.User
import com.android.virtus.chatapp.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ProfileFragment : Fragment() {
    lateinit var image_profile: CircleImageView
    var username: TextView? = null
    var reference: DatabaseReference? = null
    var fuser: FirebaseUser? = null
    var storageReference: StorageReference? = null
    var imageUri: Uri? = null
    var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        image_profile = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.username)
        storageReference = FirebaseStorage.getInstance().getReference("upload")
        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isAdded) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        username?.setText(user.name)
                    }
                    if (user != null) {
                        if (user.imageURL.equals("default")) {
                            image_profile.setImageResource(R.mipmap.ic_launcher_round)
                        } else {
                            Glide.with(context!!).load(user.imageURL).into(image_profile)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        image_profile.setOnClickListener({ OpenImage() })
        return view
    }

    private fun OpenImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = Objects.requireNonNull(context)!!.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadImage() {
        val pd = ProgressDialog(context)
        pd.setMessage("Đang tải lên...")
        pd.show()
        val fileReference = storageReference!!.child(System.currentTimeMillis().toString() + "." + imageUri?.let { getFileExtension(it) })
        uploadTask = fileReference.putFile(imageUri!!)
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw Objects.requireNonNull(task.exception)!!
            }
            fileReference.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result!!
                val mUri = downloadUri.toString()
                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
                val map = HashMap<String, Any>()
                map["imageURL"] = mUri
                reference?.updateChildren(map)
                pd.dismiss()
            } else {
                Toast.makeText(context, "Cập nhật ảnh không thành công", Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        }?.addOnFailureListener { e ->
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            pd.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && requestCode == -Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadImage()
            Toast.makeText(context, "Ảnh đang được tải lên, chờ chút", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val IMAGE_REQUEST = 1
    }
}