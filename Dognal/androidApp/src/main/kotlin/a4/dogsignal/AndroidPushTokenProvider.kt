package a4.dogsignal

import a4.dogsignal.push.PushToken
import a4.dogsignal.push.PushTokenProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPushTokenProvider : PushTokenProvider {
    override suspend fun getToken(): PushToken? =
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!continuation.isActive) return@addOnCompleteListener

                val token =
                    if (task.isSuccessful) {
                        task.result
                            ?.takeIf { it.isNotBlank() }
                    } else {
                        null
                    }

                continuation.resume(
                    token?.let {
                        PushToken(
                            platform = PLATFORM,
                            token = it,
                        )
                    },
                )
            }
        }

    companion object {
        private const val PLATFORM = "android"
    }
}
