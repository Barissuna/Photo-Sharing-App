package com.barissuna.photosharingfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun logIn(view:View){

        auth.signInWithEmailAndPassword(findViewById<EditText>(R.id.emailText).text.toString(),findViewById<EditText>(R.id.passwordText).text.toString()).
        addOnCompleteListener { task ->

            if(task.isSuccessful){
                val currentUser = auth.currentUser!!.email.toString()
                Toast.makeText(applicationContext,"Welcome: ${currentUser}",Toast.LENGTH_LONG).show()

                val intent = Intent(this,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }

    fun signUp(view:View){
        val email = findViewById<EditText>(R.id.emailText).text.toString()
        val password = findViewById<EditText>(R.id.passwordText).text.toString()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

            if(task.isSuccessful){
                val intent = Intent(this,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
}