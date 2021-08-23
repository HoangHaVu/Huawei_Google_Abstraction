package de.c24.hg_abstraction

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import de.c24.hg_abstraction.core_pushkit.NotificationHandlerCore

class NotificationHandler: NotificationHandlerCore {

    companion object {

        private const val TAG = "NotificationHandler"
    }

    override var tokenResult: ((String) -> Unit)? = null

    override fun subscribeToTopic(topic: String, context: Context){
        Log.d(TAG, "Subscribing to $topic topic")
        // [START subscribe_topic]
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribing successful"
                if (!task.isSuccessful) {
                    msg = "Subscribing failed! Try again later"
                }
                Log.d(TAG, msg)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }

    override fun unsubscribeToTopic(topic: String, context: Context) {
        Log.d(TAG, "Unsubscribing to $topic topic")
        // [START Unsubscribe_topic]
        Firebase.messaging.unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = "Unsubscribing successful"
                    if (!task.isSuccessful) {
                        msg = "Unsubscribing failed! Try again later"
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }

    }

    override fun getToken(context:Context) {
        // Get token
        // [START log_reg_token]
        Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Check whether the token is empty.
            if (!TextUtils.isEmpty(token) && token != null) {
                tokenResult?.invoke(token)
            }

            // Log and toast
            val msg = "Token created: $token"
            Log.d(TAG, msg)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        })
        // [END log_reg_token]
    }

     override fun sendUplinkMessage(context: Context){
        val fm = Firebase.messaging
        val messageId = 0 // Increment for each
        fm.send(remoteMessage("${Constants.MessagePayloadKeys.SENDER_ID}@fcm.googleapis.com") {
            setMessageId(messageId.toString())
            addData("my_message", "Hello World")
            addData("my_action", "SAY_HELLO")
        })
    }

    override fun deleteToken(context: Context) {
        // Delete token
        // [START log_delete_token]
        Firebase.messaging.deleteToken()
    }
}
