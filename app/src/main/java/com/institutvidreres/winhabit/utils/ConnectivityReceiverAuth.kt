package com.institutvidreres.winhabit.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.institutvidreres.winhabit.ui.login.AuthActivity

class ConnectivityReceiverAuth(private val activity: AuthActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = AppUtils.isInternetConnected(activity)
        activity.updateUI(isConnected)
    }
}
