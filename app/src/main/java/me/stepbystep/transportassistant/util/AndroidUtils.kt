package me.stepbystep.transportassistant.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.collections.contains
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias Parameters = Map<String, Any>

const val TAG = "TransportAssistant"

fun <T, V> ReadWriteProperty<T, V?>.withDefault(default: V) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return this@withDefault.getValue(thisRef, property) ?: default
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this@withDefault.setValue(thisRef, property, value)
    }
}

//@SuppressLint("ModifierFactoryUnreferencedReceiver")
//fun Modifier.centerWithBottomPadding(padding: Dp = 20.dp): Modifier = with(ColumnScope) {
//    align(Alignment.CenterHorizontally).padding(bottom = padding)
//}
fun ColumnScope.centerWithBottomPadding(padding: Dp = 20.dp): Modifier =
    Modifier.padding(bottom = padding).align(Alignment.CenterHorizontally)

fun <K, V> Map<K, V>.putIfAbsent(key: K, value: V): Map<K, V> = when (key) {
    in this -> this
    else -> this + (key to value)
}

fun <T : Comparable<T>> Iterable<T>.minOrException(): T = minOrNull() ?: error("$this is empty")
fun <T : Comparable<T>> Iterable<T>.maxOrException(): T = maxOrNull() ?: error("$this is empty")