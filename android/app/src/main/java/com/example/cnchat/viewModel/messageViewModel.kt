package com.example.cnchat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cnchat.repositary.messageRepositary
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class messageViewModelFactory(private val repository: messageRepositary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(messageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return messageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class messageViewModel(private val repository: messageRepositary) : ViewModel() {
    val allUsers : LiveData<List<friendsTable>> = repository.allUsers

    fun insertUser(friend: friendsTable){
        viewModelScope.launch {
            repository.insertUser(friend)
        }
    }

    fun insertMessage(message: messageTable) = viewModelScope.launch {
        repository.insertMessage(message)
    }
}



