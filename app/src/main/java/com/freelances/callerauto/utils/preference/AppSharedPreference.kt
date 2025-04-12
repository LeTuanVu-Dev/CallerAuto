package com.freelances.callerauto.utils.preference

import android.content.SharedPreferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.json.JSONArray
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AppSharedPreference(val sharedPreference: SharedPreferences) {

    private val TAG = this::class.java.simpleName

    companion object {
        const val INT_DEFAULT = -1
        const val LONG_DEFAULT = -1L
        const val STRING_DEFAULT = ""
        const val BOOLEAN_DEFAULT = false
        const val FLOAT_DEFAULT = -1f
        const val LIST_INT_DEFAULT = "[]"

        const val PREF_NAME = "SHARED_PREFERENCE_STORE"
        private const val LANGUAGE_CODE = "LANGUAGE_CODE"
        private const val LANGUAGE_NAME = "LANGUAGE_NAME"
        const val KEY_DONE_FIRST_OPEN = "KEY_DONE_FIRST_OPEN"
    }

    var languageCode: String by stringPref(key = LANGUAGE_CODE)
    var languageName: String by stringPref(key = LANGUAGE_NAME)
    var isDoneFirstOpen: Boolean by booleanPref(KEY_DONE_FIRST_OPEN, false)

    var stateEndLifted: Boolean by booleanPref("stateEndLifted", false)
    var stateMuteMicro: Boolean by booleanPref("stateMuteMicro", false)
    var stateEnableSpeaker: Boolean by booleanPref("stateEnableSpeaker", false)
    var stateHideCallName: Boolean by booleanPref("stateHideCallName", false)
    var stateAutoEnd: Boolean by booleanPref("stateAutoEnd", false)
    var stateRejectCalls: Boolean by booleanPref("stateRejectCalls", false)
    var stateAnonymousCall: Boolean by booleanPref("stateAnonymousCall", false)
    var stateRepeatList: Boolean by booleanPref("stateRepeatList", false)
    var stateRedial: Boolean by booleanPref("stateRedial", false)

    var currentNumberRepeat: Int by intPref("currentNumberRepeat", 1)
    var currentTimerEndAuto: Int by intPref("currentTimerEndAuto", 30)
    var currentTimerEndWaiting: Int by intPref("currentTimerEndWaiting", 4)
    var valueLogin: String by stringPref("valueLogin", "")

    fun sync() {
        val remoteConfig = Firebase.remoteConfig
    }


    private fun intPref(
        key: String,
        defaultValue: Int = INT_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, Int> {
        return object : ReadWriteProperty<AppSharedPreference, Int> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): Int {
                return thisRef[key, defaultValue]
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: Int
            ) {
                thisRef[key] = value
            }
        }
    }

    private fun booleanPref(
        key: String,
        defaultValue: Boolean = BOOLEAN_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, Boolean> {
        return object : ReadWriteProperty<AppSharedPreference, Boolean> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): Boolean {
                return thisRef[key, defaultValue]
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: Boolean
            ) {
                thisRef[key] = value
            }
        }
    }

    private fun longPref(
        key: String,
        defaultValue: Long = LONG_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, Long> {
        return object : ReadWriteProperty<AppSharedPreference, Long> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): Long {
                return thisRef[key, defaultValue]
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: Long
            ) {
                thisRef[key] = value
            }
        }
    }

    private fun stringPref(
        key: String,
        defaultValue: String = STRING_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, String> {
        return object : ReadWriteProperty<AppSharedPreference, String> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): String {
                return thisRef[key, defaultValue]
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: String
            ) {
                thisRef[key] = value
            }
        }
    }

    private fun listIntPref(
        key: String,
        defaultValue: String = LIST_INT_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, List<Int>> {
        return object : ReadWriteProperty<AppSharedPreference, List<Int>> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): List<Int> {
                val value = thisRef[key, defaultValue]
                return fromStringToListInteger(value)
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: List<Int>
            ) {
                thisRef[key] = fromListIntegerToString(value)
            }
        }
    }


    private fun listStringPref(
        key: String,
        defaultValue: String = LIST_INT_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, List<Int>> {
        return object : ReadWriteProperty<AppSharedPreference, List<Int>> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): List<Int> {
                val value = thisRef[key, defaultValue]
                return fromStringToListInteger(value)
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: List<Int>
            ) {
                thisRef[key] = fromListIntegerToString(value)
            }
        }
    }

    private fun floatPref(
        key: String,
        defaultValue: Float = FLOAT_DEFAULT
    ): ReadWriteProperty<AppSharedPreference, Float> {
        return object : ReadWriteProperty<AppSharedPreference, Float> {
            override fun getValue(thisRef: AppSharedPreference, property: KProperty<*>): Float {
                return thisRef[key, defaultValue]
            }

            override fun setValue(
                thisRef: AppSharedPreference,
                property: KProperty<*>,
                value: Float
            ) {
                thisRef[key] = value
            }
        }
    }

    private fun fromListIntegerToString(list: List<Int>): String {
        val jsonArray = JSONArray(list)
        return jsonArray.toString()
    }

    private fun fromStringToListInteger(jsonString: String): List<Int> {
        return run {
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { jsonArray.getInt(it) }
        }
    }
}

inline operator fun <reified T : Any> AppSharedPreference.set(
    key: String,
    value: T?
) {
    val editor = this.sharedPreference.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Int -> editor.putInt(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Long -> editor.putLong(key, value)
        else -> throw UnsupportedOperationException("Shared preference does not supported this data type ${T::class.simpleName}")
    }
    editor.apply()
}

inline operator fun <reified T : Any> AppSharedPreference.get(
    key: String,
    defaultValue: T
): T {

    return when (T::class) {
        String::class -> this.sharedPreference.getString(key, defaultValue as? String) as T
        Int::class -> this.sharedPreference.getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> this.sharedPreference.getBoolean(
            key,
            defaultValue as? Boolean ?: false
        ) as T

        Float::class -> this.sharedPreference.getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> this.sharedPreference.getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Shared preference does not supported this data type ${T::class.simpleName}")
    }
}
