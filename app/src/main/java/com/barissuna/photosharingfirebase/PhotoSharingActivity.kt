package com.barissuna.photosharingfirebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PhotoSharingActivity : AppCompatActivity() {

    var selectedImage : Uri ?= null
    var selectedBitmap : Bitmap ?= null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_sharing)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
1    }

    fun share(view:View){

        val uuid = UUID.randomUUID()
        val reference = storage.reference
        val imageReference = reference.child("images").child("${uuid}.jpg")

        if(selectedImage != null){
            imageReference.putFile(selectedImage!!).addOnSuccessListener { taskSnapShot ->
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child("${uuid}.jpg")
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val currentUserComment = findViewById<EditText>(R.id.commentText).text.toString()
                    val date = Timestamp.now()

                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("imageUrl",downloadUrl)
                    postHashMap.put("userEmail",currentUserEmail)
                    postHashMap.put("userComment",currentUserComment)
                    postHashMap.put("date",date)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener {exception ->
                        Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }
            }
        }


    }

    fun selectImage(view:View){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeryIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){

            selectedImage = data.data
            if(selectedImage != null){
                if(Build.VERSION.SDK_INT >= 28){

                    val source = ImageDecoder.createSource(this.contentResolver,selectedImage!!)
                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(selectedBitmap)

                }
                else{
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(selectedBitmap)
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


}