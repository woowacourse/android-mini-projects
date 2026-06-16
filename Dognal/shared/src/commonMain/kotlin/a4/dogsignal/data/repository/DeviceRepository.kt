package a4.dogsignal.data.repository

import a4.dogsignal.data.local.DeviceLocalDataSource
import a4.dogsignal.data.network.DeviceDataSource
import a4.dogsignal.push.NoOpPushTokenProvider
import a4.dogsignal.push.PushTokenProvider

class DeviceRepository(
    private val localDataSource: DeviceLocalDataSource,
    private val networkDataSource: DeviceDataSource,
    private val pushTokenProvider: PushTokenProvider = NoOpPushTokenProvider,
) {
    fun getDeviceId(): String? = localDataSource.getDeviceId()

    suspend fun authenticate(
        code: String,
        secret: String,
    ): String {
        val deviceId = networkDataSource.getDeviceId(code, secret)
        localDataSource.saveDeviceId(deviceId)
        registerPushToken(code, secret)
        return deviceId
    }

    private suspend fun registerPushToken(
        code: String,
        secret: String,
    ) {
        val pushToken = pushTokenProvider.getToken() ?: return
        val installationId = localDataSource.getOrCreateInstallationId()

        runCatching {
            networkDataSource.registerPushToken(
                code = code,
                secret = secret,
                installationId = installationId,
                platform = pushToken.platform,
                fcmToken = pushToken.token,
            )
        }.onFailure { cause ->
            println("Failed to register push token: ${cause.message}")
        }
    }
}
