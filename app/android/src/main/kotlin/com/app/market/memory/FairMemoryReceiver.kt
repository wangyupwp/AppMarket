package com.app.market.memory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Parcel
import android.os.Process
import android.util.Log
import androidx.core.content.ContextCompat

internal class FairMemoryReceiver(
    private val trimCaches: () -> Unit,
    private val saveCheckpoint: () -> Boolean,
) : BroadcastReceiver() {
    fun register(context: Context) {
        val thread = HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND).apply { start() }
        val filter = IntentFilter(ACTION_TRIM).apply { addAction(ACTION_KILL) }
        try {
            ContextCompat.registerReceiver(
                context, this, filter, null, Handler(thread.looper), ContextCompat.RECEIVER_EXPORTED,
            )
        } catch (error: RuntimeException) {
            thread.quitSafely()
            Log.w(TAG, "Could not register fair memory receiver", error)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != ACTION_TRIM && action != ACTION_KILL) return
        if (Build.VERSION.SDK_INT >= 34) {
            val uid = sentFromUid
            // Some firmware does not disclose the sender. When it does, accept system senders only.
            if (uid >= 0 && uid != Process.SYSTEM_UID &&
                context.packageManager.checkSignatures(uid, Process.SYSTEM_UID) != PackageManager.SIGNATURE_MATCH
            ) return
        }
        val request = try {
            val common = intent.getBundleExtra("common") ?: return
            val type = common.getInt("notifyType")
            if (type != TYPE_PSS && type != TYPE_HEAP) return
            if (!common.containsKey("notifyId")) return
            Request(type, common.getInt("notifyId"), common.getBinder("callback") ?: return)
        } catch (error: RuntimeException) {
            Log.w(TAG, "Invalid fair memory broadcast", error)
            return
        }

        val handled = try {
            if (action == ACTION_KILL) saveCheckpoint()
            else {
                trimCaches()
                true
            }
        } catch (error: Exception) {
            Log.w(TAG, "Fair memory request failed", error)
            false
        }
        request.reply(handled)
    }

    private data class Request(val type: Int, val id: Int, val callback: IBinder) {
        fun reply(handled: Boolean) {
            val data = Parcel.obtain()
            try {
                data.writeInt(type)
                data.writeInt(id)
                data.writeInt(if (handled) 0 else 1)
                data.writeBundle(Bundle())
                if (!callback.transact(IBinder.FIRST_CALL_TRANSACTION, data, null, IBinder.FLAG_ONEWAY)) {
                    Log.w(TAG, "Fair memory callback was not handled")
                }
            } catch (error: Exception) {
                Log.w(TAG, "Fair memory callback failed", error)
            } finally {
                data.recycle()
            }
        }
    }

    private companion object {
        const val TAG = "FairMemory"
        const val ACTION_TRIM = "itgsa.intent.action.TRIM"
        const val ACTION_KILL = "itgsa.intent.action.KILL"
        const val TYPE_PSS = 1000
        const val TYPE_HEAP = 2000
    }
}
