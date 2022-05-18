package com.example.zelinn

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.cioccarellia.ksprefs.KsPrefs
import com.cioccarellia.ksprefs.config.EncryptionType
import com.cioccarellia.ksprefs.config.model.KeySize
import com.cioccarellia.ksprefs.config.model.KeyTagSize
import java.security.SecureRandom
import java.security.Security

class ZelinnApp: Application() {

    companion object {
        lateinit var appContext: Context
        val prefs by lazy {
            return@lazy KsPrefs(appContext) {
                encryptionType=EncryptionType.KeyStore(android.os.Process.myUid().toString())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}