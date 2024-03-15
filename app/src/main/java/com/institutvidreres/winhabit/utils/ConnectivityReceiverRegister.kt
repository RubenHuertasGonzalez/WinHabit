package com.institutvidreres.winhabit.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.institutvidreres.winhabit.ui.login.RegisterActivity

class ConnectivityReceiverRegister(private val activity: RegisterActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = AppUtils.isInternetConnected(activity)
        activity.updateUI(isConnected)
    }
}