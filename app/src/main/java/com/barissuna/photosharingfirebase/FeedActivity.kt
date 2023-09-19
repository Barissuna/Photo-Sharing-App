package com.barissuna.photosharingfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var recyclerViewAdapter : FeedRecyclerAdapter
    var postListesi = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getData()
        var layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager= layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postListesi)
        findViewById<RecyclerView>(R.id.recyclerView).adapter=recyclerViewAdapter
    }

    fun getData(){
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if(error != null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null && !snapshot.isEmpty){
                    val documents = snapshot.documents
                    postListesi.clear()
                    for(document in documents){
                        val userEmail = document.get("userEmail") as String
                        var userComment = document.get("userComment") as String
                        val imageUrl = document.get("imageUrl") as String
                        val downloadedImage = Post(userEmail,userComment,imageUrl)
                        postListesi.add(downloadedImage)
                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.share_photo){
            val intent = Intent(this,PhotoSharingActivity::class.java)
            startActivity(intent)
        }
        else if(item.itemId == R.id.log_out){
            auth.signOut()
            val intent = Intent(this,UserActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}