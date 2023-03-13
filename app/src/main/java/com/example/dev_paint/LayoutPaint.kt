package com.example.dev_paint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class LayoutPaint : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_paint)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        frameLayout = findViewById(R.id.frameLayout)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_dibujar -> {
                    switchFragment(DibujarFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.item_imitar -> {
                    switchFragment(ImitarFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.item_confi -> {
                    switchFragment(ConfiguracionesFragment())
                    return@setOnItemSelectedListener true
                }
            }

            false
        }

        bottomNavigationView.selectedItemId = R.id.item_dibujar
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}