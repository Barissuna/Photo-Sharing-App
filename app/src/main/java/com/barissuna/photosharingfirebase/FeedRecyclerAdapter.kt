package com.barissuna.photosharingfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return  PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.recycler_row_user_email).text = postList[position].userEmail
        holder.itemView.findViewById<TextView>(R.id.recycler_row_user_comment).text = postList[position].userComment
        Picasso.get().load(postList[position].imageUrl).into(holder.itemView.findViewById<ImageView>(R.id.recycler_row_imageview))
    }
}