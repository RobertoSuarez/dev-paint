package com.example.dev_paint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.dev_paint.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class LayoutPaint : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout
    private var rol: String = "Docente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_paint)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        frameLayout = findViewById(R.id.frameLayout)

        val menu: Menu = bottomNavigationView.menu
        val menuItemAsignar: MenuItem = menu.findItem(R.id.item_asignar)

        menuItemAsignar.isVisible = false

        User().getCurrentUser()
            .addOnSuccessListener { document ->
                val usuario = document.toObject(User::class.java)
                this.rol = usuario?.rol.toString()

                if (usuario?.rol == "Docente") {
                    menuItemAsignar.isVisible = true
                    menuItemAsignar.setIcon(R.drawable.baseline_add_task_24)
                }

                if (usuario?.rol == "Estudiante") {
                    menuItemAsignar.isVisible = true
                    menuItemAsignar.title = "Tareas"
                    menuItemAsignar.setIcon(R.drawable.baseline_task_24)
                }
            }

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
                // establecemos el fragment Calificaciones
                R.id.item_puntaje -> {
                    switchFragment(CalificacionesFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.item_asignar -> {
                    if (rol == "Docente") {
                        switchFragment(AsignarTareaFragment())
                    } else {
                        switchFragment(TareasFragment())
                    }

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