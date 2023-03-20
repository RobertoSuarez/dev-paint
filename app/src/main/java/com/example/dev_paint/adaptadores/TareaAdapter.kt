package com.example.dev_paint.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dev_paint.models.Tarea
import com.example.dev_paint.R

class TareaAdapter(private val tareas: List<Tarea>) : RecyclerView.Adapter<TareaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea)
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgTarea: ImageView = itemView.findViewById(R.id.img_tarea)
        private val txtNombreEstudiante: TextView = itemView.findViewById(R.id.nombre_estudiante)
        private val txtNombreProfesor: TextView = itemView.findViewById(R.id.nombre_profesor)
        private val txtEstado: TextView = itemView.findViewById(R.id.estado_tarea)
        private val txtFecha: TextView = itemView.findViewById(R.id.fecha_tarea)

        fun bind(tarea: Tarea) {
            // Cargar la imagen en el ImageView
            Glide.with(itemView.context)
                .load(tarea.urlImgen)
                .into(imgTarea)

            // Establecer los valores de los campos de la tarea en los TextViews
            txtNombreEstudiante.text = tarea.nombreEstudiante
            txtNombreProfesor.text = tarea.nombreProfesor
            txtEstado.text = tarea.estado
            txtFecha.text = tarea.fecha.toString()

        }
    }
}
