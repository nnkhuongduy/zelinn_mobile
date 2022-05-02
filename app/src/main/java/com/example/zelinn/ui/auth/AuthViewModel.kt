package com.example.zelinn.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {
    private val mutableEmail = MutableLiveData<String>()
    val email: MutableLiveData<String> get() = mutableEmail

    fun setEmail(email: String) {
        mutableEmail.value = email
    }
}