package com.xiaochen.wechatforwardclient

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.xiaochen.App

/**
 * Created by tanfujun on 5/24/17.
 */

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                if (pdus.isEmpty()) {
                    return
                }
                val messages = arrayOfNulls<SmsMessage>(pdus.size)
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    sb.append(messages[i]?.messageBody)
                }
                val sender = messages[0]?.originatingAddress
                val message = sb.toString()

                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                App.smsPublishSubject.onNext(message);

                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }

    companion object {
        private val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
}
