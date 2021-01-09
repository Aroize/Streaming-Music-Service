package ru.ilia.streamer.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.ilia.streamer.R
import ru.ilia.streamer.core.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFirstLaunch()) {
            launchHelperActivity()
        }
        setContentView(R.layout.main_activity)
        findViewById<Button>(R.id.stream).setOnClickListener {
            StreamingActivity.launch(this)
        }
    }

    private fun isFirstLaunch() = PreferenceManager.getBoolean(HelperActivity.HAD_LAUNCH).not()

    private fun launchHelperActivity() = HelperActivity.launch(this)
}