package a4.dogsignal.push

data class PushToken(
    val platform: String,
    val token: String,
)

interface PushTokenProvider {
    suspend fun getToken(): PushToken?
}

object NoOpPushTokenProvider : PushTokenProvider {
    override suspend fun getToken(): PushToken? = null
}
