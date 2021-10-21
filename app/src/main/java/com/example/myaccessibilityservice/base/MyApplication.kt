package com.example.myaccessibilityservice.base

import android.app.Application

class MyApplication: Application(){
    companion object {}
    override fun onCreate() {
        super.onCreate()
    }
}