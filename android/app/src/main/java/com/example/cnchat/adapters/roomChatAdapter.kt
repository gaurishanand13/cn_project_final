package com.example.cnchat.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cnchat.R
import com.example.cnchat.model.myMessage
import com.example.cnchat.socketHelper


public interface roomChatInterface{
    fun onClick(position: Int)
}

class ViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView)

class chatRoomAdapter(val context: Context,val myInterface : roomChatInterface,val chatList : ArrayList<myMessage>): RecyclerView.Adapter<ViewHolder>() {

    var added_or_left_textView : TextView ? =null
    var nickname: TextView? = null
    var message_one: TextView? = null
    var message_two: TextView? = null
    var relativeLayoutMessageOthers : RelativeLayout? = null
    var relativeLayoutMessageMe : RelativeLayout?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = layoutInflater.inflate(R.layout.chat_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        added_or_left_textView = holder.itemView.findViewById(R.id.added_or_left_textView)
        relativeLayoutMessageOthers = holder.itemView.findViewById(R.id.relativeLayoutMessageOthers)
        relativeLayoutMessageMe = holder.itemView.findViewById(R.id.relativeLayoutMessageMe)
        nickname = holder.itemView.findViewById(R.id.name)
        message_one = holder.itemView.findViewById(R.id.message_body)
        message_two = holder.itemView.findViewById(R.id.message_body_two)

        nickname!!.text = chatList.get(position).sendersUserName
        message_one!!.text = chatList.get(position).messageContent
        message_two!!.text = chatList.get(position).messageContent

        if(chatList.get(position).sendersUserName.equals(socketHelper.userName)){
            relativeLayoutMessageOthers!!.visibility = View.GONE
            added_or_left_textView!!.visibility = View.GONE
            relativeLayoutMessageMe!!.visibility = View.VISIBLE

        }
        else{
            relativeLayoutMessageOthers!!.visibility = View.VISIBLE
            relativeLayoutMessageMe!!.visibility = View.GONE
            added_or_left_textView!!.visibility = View.GONE
        }

        if(chatList.get(position).messageContent.equals("ADDED TO GROUP" )|| chatList.get(position).messageContent.equals("LEFT GROUP" )){
            relativeLayoutMessageOthers!!.visibility = View.GONE
            relativeLayoutMessageMe!!.visibility = View.GONE
            added_or_left_textView!!.visibility = View.VISIBLE

            if(chatList.get(position).messageContent.equals("ADDED TO GROUP" )){
                added_or_left_textView!!.text = chatList.get(position).sendersUserName + " Added"
            }
            else{
                added_or_left_textView!!.text = chatList.get(position).sendersUserName + " Left"
            }

            //holder.itemView.findViewById<LinearLayout>(R.id.linearLayout).gravity = Gravity.CENTER
        }
    }
}