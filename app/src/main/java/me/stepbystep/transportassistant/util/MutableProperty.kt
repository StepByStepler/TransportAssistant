package me.stepbystep.transportassistant.util

import android.content.Context
import androidx.compose.runtime.MutableState
import kotlin.reflect.KMutableProperty0

sealed class MutableProperty<T> {

    class Kotlin<T>(private val property: KMutableProperty0<T>) : MutableProperty<T>() {
        override fun get(): T = this.property.get()

        override fun set(value: T) {
            this.property.set(value)
        }
    }

    class State<T>(private val state: MutableState<T>) : MutableProperty<T>() {
        override fun get(): T = state.value

        override fun set(value: T) {
            state.value = value
        }
    }

    class Shared<T>(
        private val property: SharedPreferencesProperty<T>,
        private val context: Context,
    ) : MutableProperty<T?>() {

        override fun get(): T? {
            return property.getValue(context, ::property)
        }

        override fun set(value: T?) {
            property.setValue(context, ::property, value)
        }
    }

    class Mapping<T, R>(
        private val baseProperty: MutableProperty<T>,
        private val toNew: (T) -> R,
        private val toOld: (R) -> T,
    ) : MutableProperty<R>() {

        override fun get(): R = toNew(baseProperty.get())

        override fun set(value: R) {
            baseProperty.set(toOld(value))
        }
    }

    abstract fun get(): T

    abstract fun set(value: T)
}

fun <T> MutableState<T>.asMutableProperty(): MutableProperty<T> =
    MutableProperty.State(this)

fun <T> KMutableProperty0<T>.asMutableProperty(): MutableProperty<T> =
    MutableProperty.Kotlin(this)

fun <T> SharedPreferencesProperty<T>.asMutableProperty(context: Context): MutableProperty<T?> =
    MutableProperty.Shared(this, context)

fun <T, R> MutableProperty<T>.map(toNew: (T) -> R, toOld: (R) -> T): MutableProperty<R> =
    MutableProperty.Mapping(this, toNew, toOld)

fun <T> MutableProperty<T?>.withDefault(value: T): MutableProperty<T> = map({ it ?: value }, { it })