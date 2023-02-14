package redstone.capacitorcracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.children
import com.google.android.material.switchmaterial.SwitchMaterial
import redstone.capacitorcracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wsClient: WSClient
    private val relaySwitches: ArrayList<SwitchMaterial> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSetTimeLen.setOnClickListener(SetTimeLengthListener(binding, this))
        binding.relaySwitchesLayout.children.forEach { relaySwitches.add(it as SwitchMaterial) }


        wsClient = WSClient()
        ButtonHandler.wsClient = wsClient
        RelaySwitchListener.wsClient = wsClient

        for (i in 0 until relaySwitches.size) {
            relaySwitches[i].setOnCheckedChangeListener(RelaySwitchListener(this, i + 1))
        }

        StatusRefresher(binding, this, relaySwitches, wsClient).start()
    }

}