package com.example.cnchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cnchat.R
import com.example.cnchat.model.myMessage


public interface roomChatInterface{
    fun onClick(position: Int)
}

class ViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView)

class chatRoomAdapter(val context: Context,val myInterface : roomChatInterface,val chatList : ArrayList<myMessage>): RecyclerView.Adapter<ViewHolder>() {

    var nickname: TextView? = null
    var message: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = layoutInflater.inflate(R.layout.chat_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        nickname = holder.itemView.findViewById(R.id.nickname)
        message = holder.itemView.findViewById(R.id.message)

        nickname!!.text = chatList.get(position).sendersUserName
        message!!.text = chatList.get(position).messageContent
    }
}