package com.example.dev_paint.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dev_paint.R
import com.example.dev_paint.models.Calificacion
import java.text.SimpleDateFormat
import java.util.Locale

class CalifiacionesAdaptador (private val data: List<Calificacion>) : RecyclerView.Adapter<CalifiacionesAdaptador.CalificacionesHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalificacionesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calificacion, parent, false)

        return CalificacionesHolder(view)
    }

    override fun onBindViewHolder(holder: CalificacionesHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    class CalificacionesHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
        private val nameView: TextView = itemView.findViewById(R.id.name_view)
        private val scoreView: TextView = itemView.findViewById(R.id.score_view)
        private val dateView: TextView = itemView.findViewById(R.id.date_view)

        fun bind(item: Calificacion) {
            //imageView.setImageResource(item.)
            Glide.with(itemView.context).load(item.img1).into(imageView)
            nameView.text = item.name
            scoreView.text = item.rating.toString()

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = sdf.format(item.fecha) // Formatear la fecha al formato deseado

            dateView.text = fechaFormateada
        }

    }
}