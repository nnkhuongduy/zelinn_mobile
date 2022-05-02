package com.example.zelinn

import android.app.Application
import com.orhanobut.hawk.Hawk

class ZelinnApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()
    }
}