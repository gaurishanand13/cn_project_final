package com.example.cnchat.viewModel

import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cnchat.repositary.messageRepositary
import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class particularChatViewModelFactory(private val repository: messageRepositary) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(particularChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return particularChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class particularChatViewModel(private val repository: messageRepositary) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insertMessage(message: messageTable) = viewModelScope.launch {
        repository.insertMessage(message)
    }

    //Use this for a particular chat activity to fetch all the messages from the database which are continuously changing.
    fun getCurrentMessagesLiveData(friendsUserName : String) : LiveData<List<messageTable>> {
        val allMessages: LiveData<List<messageTable>> = repository.getCurrentMessagesLiveData(friendsUserName)
        return allMessages
    }

    fun insertUser(friend: friendsTable){
        viewModelScope.launch {
            repository.insertUser(friend)
        }
    }
}
