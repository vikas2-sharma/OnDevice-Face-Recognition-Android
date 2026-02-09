package com.ml.shubham0204.facenet_android.sdk

import android.app.Activity
import android.content.Intent
import com.ml.shubham0204.facenet_android.MainActivity
import com.ml.shubham0204.facenet_android.sdk.constants.ROUTE_REGISTER
import com.ml.shubham0204.facenet_android.sdk.constants.ROUTE_VALIDATE
import com.ml.shubham0204.facenet_android.sdk.constants.TARGET_ROUTE

class FaceAuthImpl : FaceAuth{
    override fun validate(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(TARGET_ROUTE, ROUTE_VALIDATE)
        activity.startActivity(intent)
    }

    override fun register(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(TARGET_ROUTE, ROUTE_REGISTER)
        activity.startActivity(intent)
    }
}