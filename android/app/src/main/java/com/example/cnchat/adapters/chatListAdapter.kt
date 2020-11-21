package com.example.cnchat.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cnchat.R
import com.example.cnchat.constants
import com.example.cnchat.room.models.friendsTable
import kotlinx.android.synthetic.main.chat_list_item.view.*
import java.text.SimpleDateFormat


public interface chatListInterface{
    fun onClick(position: Int)
}

class chatListAdapter(val context: Context, val myInterface: chatListInterface, val userNameList: ArrayList<friendsTable>): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = layoutInflater.inflate(R.layout.chat_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = userNameList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.chat_list_item_username_textView.text = "${userNameList.get(position).friendsFirstName} ${userNameList.get(position).friendslastName}"
        holder.itemView.last_message_exchanged.text = userNameList.get(position).lastMessageExchanged
        holder.itemView.time.text = "${userNameList.get(position).dateOfMessage} ${userNameList.get(position).timeOfMessage}"
        holder.itemView.setOnClickListener {
            myInterface.onClick(position)
        }
    }
}