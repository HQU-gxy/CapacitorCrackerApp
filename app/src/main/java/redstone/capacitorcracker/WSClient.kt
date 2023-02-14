package redstone.capacitorcracker

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class WSClient : WebSocketClient(URI.create("ws://192.168.4.1:11451")) {
    private lateinit var dataJson: JSONObject
    private var dataAvailable = false

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i("WSClient", "Connected")
    }

    override fun onMessage(message: String?) {
        Log.i("WSClient", "Message: $message")
        try {
            if (message == null) return
            dataJson = JSONObject(message)
            dataAvailable = true
        } catch (ex: Exception) {
            Log.e("Problem", ex.toString())
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i("WSClient", "Closed")
    }

    override fun onError(ex: Exception?) {
        Log.i("WSClient", "Error: $ex")
    }

    fun available(): Boolean {
        return dataAvailable
    }

    fun getData(): JSONObject {
        dataAvailable = false
        return dataJson
    }
}