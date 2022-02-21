package me.stepbystep.transportassistant.network

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import com.android.volley.toolbox.StringRequest
import me.stepbystep.transportassistant.util.Parameters
import me.stepbystep.transportassistant.util.TAG
import me.stepbystep.transportassistant.util.putIfAbsent
import java.net.URLEncoder

class HttpClient(private val getLogin: () -> String?) {
    companion object {
        private const val HTTP_URL = "http://85.209.88.17:8084"
    }

    private val requestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack())).also {
        it.start()
    }

    private fun errorHandler(url: String) = Response.ErrorListener {
        Log.e(TAG, "Unable to perform HTTP request to $url. Status: ${it.networkResponse.statusCode} Response: ${it.networkResponse}, cause: ${it.cause}")
        it.cause?.printStackTrace()
    }

    fun get(path: String, params: Parameters = emptyMap(), listener: (String) -> Unit) {
        sendRequest(path, params, Request.Method.GET, listener)
    }

    fun post(path: String, params: Parameters) {
        sendRequest(path, params, Request.Method.POST) {
            // nothing
        }
    }

    private fun sendRequest(
        path: String,
        params: Parameters,
        method: Int,
        listener: (String) -> Unit,
    ) {
        val login = getLogin() ?: error("Cannot send HTTP request without login")
        val paramsWithLogin = params.putIfAbsent("login", login)
        val url = buildUrl(path, paramsWithLogin)
        val request = StringRequest(method, url, listener, errorHandler(url))
        Log.i(TAG, "Performing HTTP request on $url")

        request.setShouldCache(false)
        requestQueue.add(request)
    }

    private fun buildUrl(path: String, params: Parameters): String =
        "$HTTP_URL$path?${params.toHttpString()}"

    private fun Parameters.toHttpString(): String = toList().joinToString(separator = "&") {
        val data = URLEncoder.encode(it.second.toString(), "UTF-8")
        "${it.first}=$data"
    }
}