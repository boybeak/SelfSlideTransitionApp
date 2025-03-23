package com.github.boybeak.selfslideapp

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding

class MainActivity : AppCompatActivity() {

    private val containerAnimated by lazy { findViewById<LinearLayout>(R.id.containerAnimated) }
    private val container by lazy { findViewById<LinearLayout>(R.id.container) }
    private val addBtn by lazy { findViewById<Button>(R.id.addBtn) }
    private val selfSlide = SelfSlide.Builder()
        .inFrom(SelfSlide.START)
        .outTo(SelfSlide.BOTTOM)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addBtn.setOnClickListener {
            val view1 = createView(containerAnimated.childCount.toString())
            view1.setOnClickListener {
                containerAnimated.removeView(it)
            }
            containerAnimated.addView(view1, 0)

            val view2 = createView(container.childCount.toString())
            view2.setOnClickListener {
                container.removeView(it)
            }
            selfSlide.appearingTarget = view2
            container.addView(view2, 0)
        }

        container.layoutTransition = selfSlide

    }

    private fun createView(text: String): View {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            this.text = text
            textSize = 32f
            setPadding(32)
            gravity = Gravity.CENTER
            setBackgroundColor(randomColor())
        }
    }

}