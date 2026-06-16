package a4.dogsignal.data.local

import com.russhwolf.settings.Settings
import kotlin.random.Random

class DeviceLocalDataSource {
    private val settings = Settings()

    fun getDeviceId(): String? = settings.getStringOrNull(KEY_DEVICE_ID)

    fun getOrCreateInstallationId(): String {
        settings.getStringOrNull(KEY_INSTALLATION_ID)?.let { return it }

        val installationId = generateUuidV4()
        settings.putString(KEY_INSTALLATION_ID, installationId)
        return installationId
    }

    fun saveDeviceId(deviceId: String) {
        settings.putString(KEY_DEVICE_ID, deviceId)
    }

    private fun generateUuidV4(): String {
        val bytes = ByteArray(UUID_BYTE_SIZE)
        Random.Default.nextBytes(bytes)

        bytes[6] = ((bytes[6].toInt() and VERSION_MASK) or VERSION_4).toByte()
        bytes[8] = ((bytes[8].toInt() and VARIANT_MASK) or VARIANT_BITS).toByte()

        return buildString(UUID_STRING_LENGTH) {
            bytes.forEachIndexed { index, byte ->
                if (index in UUID_DASH_POSITIONS) append('-')
                appendByteHex(byte)
            }
        }
    }

    private fun StringBuilder.appendByteHex(byte: Byte) {
        val value = byte.toInt() and BYTE_MASK
        append(HEX_CHARS[value ushr HEX_SHIFT])
        append(HEX_CHARS[value and HEX_MASK])
    }

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_INSTALLATION_ID = "installation_id"
        private const val UUID_BYTE_SIZE = 16
        private const val UUID_STRING_LENGTH = 36
        private const val BYTE_MASK = 0xff
        private const val HEX_SHIFT = 4
        private const val HEX_MASK = 0x0f
        private const val VERSION_MASK = 0x0f
        private const val VERSION_4 = 0x40
        private const val VARIANT_MASK = 0x3f
        private const val VARIANT_BITS = 0x80
        private val UUID_DASH_POSITIONS = setOf(4, 6, 8, 10)
        private const val HEX_CHARS = "0123456789abcdef"
    }
}
