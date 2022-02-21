package me.stepbystep.transportassistant.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val SHARED_PREFERENCES_NAME = "TransportAssistant"

class SharedPreferencesProperty<T>(
    private val key: String,
    private val clazz: Class<T>,
) : ReadWriteProperty<Context, T?> {

    private companion object {
        private val GSON = Gson()
    }

    private var hasCachedValue = false
    private var cachedValue: T? = null
        set(value) {
            field = value
            hasCachedValue = true
        }

    private val Context.sharedPreferences: SharedPreferences
        get() = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getValue(thisRef: Context, property: KProperty<*>): T? {
        if (hasCachedValue) return cachedValue

        val sharedPreferences = thisRef.sharedPreferences
        val json = sharedPreferences.getString(key, null) ?: return null
        return GSON.fromJson(json, clazz)
    }

    override fun setValue(thisRef: Context, property: KProperty<*>, value: T?) {
        cachedValue = value

        thisRef.sharedPreferences.edit(commit = true) {
            if (value != null) {
                putString(key, GSON.toJson(value))
            } else {
                remove(key)
            }
        }
    }
}

inline fun <reified T> sharedProperty(key: String) = SharedPreferencesProperty(key, T::class.java)