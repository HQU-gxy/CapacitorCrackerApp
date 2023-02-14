package redstone.capacitorcracker

import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import org.json.JSONObject
import redstone.capacitorcracker.databinding.ActivityMainBinding

abstract class ButtonHandler : OnClickListener {
    companion object {
        @JvmStatic
        lateinit var wsClient: WSClient

        //set by WSClient
        @JvmStatic
        var response = 0

        @JvmStatic
        var responseAvailable = false
    }

    abstract override fun onClick(p0: View?)
}

/*
class ChargeListener(val activity: MainActivity) : ButtonHandler() {
    override fun onClick(p0: View?) {
        if (wsClient.isOpen) {
            val thisButton = p0 as Button
            val dataJson = JSONObject()
            if (thisButton.text == activity.resources.getString(R.string.start_charging))
                dataJson.put("cmd", "start_charge")
            else
                dataJson.put("cmd", "stop_charge")

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
                                    activity.resources.getString(R.string.set_it),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        1 -> {
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    if (thisButton.text == activity.resources.getString(R.string.start_charging))
                                        activity.resources.getString(R.string.already_charging)
                                    else activity.resources.getString(R.string.not_charging),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        2 -> {
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    activity.resources.getString(R.string.volt_toooooo_high),
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
*/
class SetTimeLengthListener(
    private val binding: ActivityMainBinding,
    private val activity: MainActivity,
) : ButtonHandler() {
    override fun onClick(p0: View?) {
        val timeLen = binding.textTimeLen.text.toString().toInt()
        if (timeLen > 65535) {
            Toast.makeText(
                binding.root.context,
                binding.root.context.resources.getString(R.string.time_toooooo_long),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val dataJson = JSONObject()
        val argsJson = JSONObject()
        argsJson.put("time_length", timeLen)
        dataJson.put("cmd", "set_time_length")
        dataJson.put("args", argsJson)
        Thread {
            wsClient.send(dataJson.toString())
            val t0 = System.currentTimeMillis()
            while (!responseAvailable) {
                if (System.currentTimeMillis() - t0 > 4000)
                    break

                Thread.sleep(200)
            }
            if (responseAvailable) {
                responseAvailable = false
                if (response == 0) {
                    activity.runOnUiThread {
                        Toast.makeText(
                            activity,
                            activity.resources.getString(R.string.set_it),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else
                    Log.i("SetTimeLengthListener", "response = $response")

                return@Thread
            }
            activity.runOnUiThread {
                Toast.makeText(
                    binding.root.context,
                    binding.root.context.resources.getString(R.string.timeout),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.start()

    }

}

