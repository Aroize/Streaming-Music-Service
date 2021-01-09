package ru.ilia.streamer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.ilia.streamer.R
import ru.ilia.streamer.core.PreferenceManager
import ru.ilia.streamer.core.ViewUtils

class HelperActivity : AppCompatActivity() {

    private var currentTipIndex = 0

    private val animatedViewIds = intArrayOf(
        R.id.first_tip,
        R.id.streaming_tip,
        R.id.connecting_tip,
        R.id.last_tip
    )

    private lateinit var views: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helper_activity)
        setTitle(R.string.tips)
        views = animatedViewIds.map { findViewById<View>(it) }
        val nextTip = findViewById<Button>(R.id.next_tip)
        nextTip.setOnClickListener {
            showNextTip()
        }
    }

    override fun finish() {
        super.finish()
        PreferenceManager.setHasBeenLaunchedBefore()
    }

    private fun showNextTip() {
        if (currentTipIndex < views.size) {
            ViewUtils.showViewAnimated(views[currentTipIndex++])
        } else {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            val intent = Intent(context, HelperActivity::class.java)
            context.startActivity(intent)
        }
    }
}