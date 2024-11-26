package com.example.chambua.presentation.session

import android.content.Context
import android.content.Intent

object ServiceHelper {
    fun triggerForegroundService(context: Context, action: String){
        Intent(context, SessionTimer::class.java).apply{
            this.action = action
            context.startService(this)
        }
    }
}