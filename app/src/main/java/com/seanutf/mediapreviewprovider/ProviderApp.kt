package com.seanutf.mediapreviewprovider

import android.app.Application

class ProviderApp : Application() {


    override fun onCreate() {
        super.onCreate()
        ProviderContext.context = this
    }
}