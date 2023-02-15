package redstone.capacitorcracker

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.material.switchmaterial.SwitchMaterial
import org.java_websocket.enums.ReadyState
import org.json.JSONObject
import redstone.capacitorcracker.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class StatusRefresher(
    private val binding: ActivityMainBinding,
    private val activity: MainActivity,
    private val relaySwitches: ArrayList<SwitchMaterial>,
    private val theClient: WSClient
) : Thread() {
    override fun run() {
        while (true) {
            if (!theClient.isOpen) {

                activity.runOnUiThread {
                    relaySwitches.forEach { it.isEnabled = false }
                    binding.buttonSetTimeLen.isEnabled = false
                    binding.toolbar.setNavigationIcon(R.drawable.disconnected)
                }
                Log.i("ReadyState", theClient.readyState.toString())
                try {
                    theClient.connectBlocking(2000, TimeUnit.MILLISECONDS)
                    theClient.closeBlocking()
                } catch (e: Exception) {
                    if (theClient.readyState.equals(ReadyState.CLOSED) ||
                        theClient.readyState.equals(ReadyState.CLOSING)
                    ) {
                        Log.i("WSClient", "Reconnecting")
                        theClient.reconnectBlocking()
                        continue
                    }
                }
            } else {
                if (!binding.buttonSetTimeLen.isEnabled)
                    activity.runOnUiThread {
                        binding.toolbar.setNavigationIcon(R.drawable.connected)
                        relaySwitches.forEach { it.isEnabled = true }
                        binding.buttonSetTimeLen.isEnabled = true
                    }

                if (theClient.available()) {
                    val dataJson = theClient.getData()
                    for (k in dataJson.keys()) {
                        parseData(dataJson, k)
                    }
                }
            }

            sleep(1000)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun parseData(dataJson: JSONObject, k: String) {
        when (k) {
            "voltage" -> {
                val voltage = dataJson.getInt(k)
                activity.runOnUiThread {

                    binding.textVoltage.setText(String.format("%.2f", voltage * 10.7 / 1024f))
                    binding.textBatteryHint.setText(
                        if (voltage < 652) R.string.low_batt
                        else R.string.not_low_batt
                    )
                }
            }
            "relays_status" -> {
                val relayStatus = dataJson.getJSONArray(k)

                activity.runOnUiThread {
                    for (foo in 0 until relaySwitches.size) {
                        //Log.i("RelayStatus", "$foo: ${relayStatus.getInt(foo)}")
                        relaySwitches[foo].isChecked = (relayStatus.getInt(foo) == 1)
                        relaySwitches[foo].isEnabled = (relayStatus.getInt(foo) == 0)
                    }

                }
            }
            "time_length" -> {
                val timeLength = dataJson.getInt(k)
                activity.runOnUiThread {
                    binding.textCurrentLen.text = "$timeLength ms"
                }
            }
//            "fuck_status" -> {
//                RelaySwitchListener.responseAvailable = true
//                RelaySwitchListener.response = dataJson.getInt(k)
//            }

            "time_set_status" -> {
                ButtonHandler.response = dataJson.getInt(k)
                ButtonHandler.responseAvailable = true
            }
        }
    }

}

