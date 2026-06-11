package a4.dogsignal.data.local

import com.russhwolf.settings.Settings

class DeviceLocalDataSource {
    private val settings = Settings()

    fun getDeviceId(): String? = settings.getStringOrNull(KEY_DEVICE_ID)

    fun saveDeviceId(deviceId: String) {
        settings.putString(KEY_DEVICE_ID, deviceId)
    }

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
    }
}
