package com.example.llamigo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import com.example.llama.LLamaAndroid
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(private val llamaAndroid: LLamaAndroid = LLamaAndroid.instance()) : ViewModel() {
    private val tag: String? = this::class.simpleName

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    fun sendButtonPressed(messageBody: String) {
        if (messageBody.isNotEmpty()) {
            userMessage(messageBody)
            assistantMessage("")
            viewModelScope.launch {
                llamaAndroid.send(messageBody)
                    .catch {
                        Log.e(tag, "send() failed", it)
                        assistantMessage(it.message!!)
                    }
                    .collect { assistantMessageChunk(it) }
            }
        }
    }

    fun userMessage(messageBody: String) {
        val message = Message(author = Author.USER, body = messageBody)
        _messages.update { currentMessages ->
            listOf(message) + currentMessages // TODO: Is O(n); make it O(1).
        }
    }

    fun assistantMessage(messageBody: String) {
        val message = Message(author = Author.ASSISTANT, body = messageBody)
        _messages.update { currentMessages ->
            listOf(message) + currentMessages // TODO: Is O(n); make it O(1).
        }
    }

    fun assistantMessageChunk(chunk: String) {
        _messages.update { currentMessages ->
            var found = false

            currentMessages.map {
                if (it.author == Author.ASSISTANT && !found) {
                    found = true
                    it.copy(body = it.body + chunk)
                } else {
                    it
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {
            try {
                llamaAndroid.unload()
            } catch (exc: IllegalStateException) {
                assistantMessage(exc.message!!)
            }
        }
    }

    fun load(pathToModel: String) {
        viewModelScope.launch {
            try {
                llamaAndroid.load(pathToModel)
                assistantMessage("Loaded $pathToModel")
            } catch (exc: IllegalStateException) {
                Log.e(tag, "load() failed", exc)
                assistantMessage(exc.message!!)
            }
        }
    }
}