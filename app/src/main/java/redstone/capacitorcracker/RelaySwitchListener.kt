package redstone.capacitorcracker

import android.widget.CompoundButton
import android.widget.Toast
import org.json.JSONObject

class RelaySwitchListener(private val activity: MainActivity, private val relayNumber: Int) :
    CompoundButton.OnCheckedChangeListener {
    companion object {
        @JvmStatic
        lateinit var wsClient: WSClient

        //set by WSClient
        @JvmStatic
        var response = 0

        @JvmStatic
        var responseAvailable = false
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            buttonView.isEnabled = false
            val dataJson = JSONObject()
            val argsJson = JSONObject()
            argsJson.put("relay_num", relayNumber)
            dataJson.put("cmd", "fuck_relay")
            dataJson.put("args", argsJson)
            Thread {
                wsClient.send(dataJson.toString())
                val t0 = System.currentTimeMillis()
                while (!responseAvailable) {
                    if (System.currentTimeMillis() - t0 > 1000)
                        break

                    Thread.sleep(200)
                }
                if (responseAvailable) {
                    responseAvailable = false
                    when (response) {
                        0 -> {
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    activity.resources.getString(R.string.relay_on),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        1 -> {
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    activity.resources.getString(R.string.invalid_relay_num),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        2 -> {
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    activity.resources.getString(R.string.already_on),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }.start()
        }

    }
}
