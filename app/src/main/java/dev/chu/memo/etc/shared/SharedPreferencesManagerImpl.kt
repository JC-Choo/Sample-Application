package dev.chu.memo.etc.shared

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SharedPreferencesManagerImpl(val context: Context) : SharedPreferenceManager {

    private val notEncryptedPreferencesName = "not_encrypted_preferences_filename"
    private val encryptedPreferencesName = "encrypted_preferences_filename"

    private var prefs: SharedPreferences

    init {


        val nonEncryptedPreferences: SharedPreferences = context.getSharedPreferences(notEncryptedPreferencesName, Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            prefs = initializeEncryptedSharedPreferencesManager()
            if (nonEncryptedPreferences.all.isNotEmpty()) {
                // migrate non encrypted shared preferences
                // to encrypted shared preferences and clear them once finished.
                nonEncryptedPreferences.copyTo(prefs)
                nonEncryptedPreferences.clear()
            }
        } else {
            prefs = nonEncryptedPreferences
        }
    }

    private fun initializeEncryptedSharedPreferencesManager(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            encryptedPreferencesName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun <T : Any?> set(key: String, value: T) {
        prefs.set(key, value)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        val value = getValue(key, defaultValue)
        return value as String?
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = getValue(key, defaultValue)
        return value as Int
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = getValue(key, defaultValue)
        return value as Boolean
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val value = getValue(key, defaultValue)
        return value as Long
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val value = getValue(key, defaultValue)
        return value as Float
    }

    private fun getValue(key: String, defaultValue: Any?): Any? {
        var value = prefs.all[key]
        value = value ?: defaultValue
        return value
    }

    override fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    override fun remove(key: String) {
        prefs.remove(key)
    }

    override fun clear() {
        prefs.clear()
    }
}