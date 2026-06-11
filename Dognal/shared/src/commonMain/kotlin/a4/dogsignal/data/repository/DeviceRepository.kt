package a4.dogsignal.data.repository

import a4.dogsignal.data.local.DeviceLocalDataSource
import a4.dogsignal.data.network.DeviceDataSource

class DeviceRepository(
    private val localDataSource: DeviceLocalDataSource,
    private val networkDataSource: DeviceDataSource,
) {
    fun getDeviceId(): String? = localDataSource.getDeviceId()

    suspend fun authenticate(
        code: String,
        secret: String,
    ): String {
        val deviceId = networkDataSource.getDeviceId(code, secret)
        localDataSource.saveDeviceId(deviceId)
        return deviceId
    }
}
