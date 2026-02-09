package com.ml.shubham0204.facenet_android.sdk

import android.app.Activity

interface FaceAuth {
    companion object{
        private lateinit var instance: FaceAuth
        fun getInstance(): FaceAuth {
            if (!::instance.isInitialized) {
                instance = FaceAuthImpl()
            }
            return instance
        }
    }
    fun validate(activity: Activity)
    fun register(activity: Activity)
}