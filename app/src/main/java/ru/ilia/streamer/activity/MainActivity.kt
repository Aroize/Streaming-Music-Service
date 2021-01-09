package ru.ilia.streamer.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.ilia.streamer.R
import ru.ilia.streamer.core.OsUtils
import ru.ilia.streamer.core.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFirstLaunch()) {
            launchHelperActivity()
        }
        setContentView(R.layout.main_activity)
        if (OsUtils.isStreamingAvailable().not()) {
            findViewById<Button>(R.id.stream).isEnabled = false
        } else {
            findViewById<Button>(R.id.stream).setOnClickListener {
                StreamingActivity.launch(this)
            }
        }
    }

    private fun isFirstLaunch() = PreferenceManager.hasBeenLaunchedBefore().not()

    private fun launchHelperActivity() = HelperActivity.launch(this)
}