package com.example.dev_paint.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dev_paint.models.User
import com.example.dev_paint.R

class UserAdapter(val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder para cada item del RecyclerView
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.user_name_textview)
        val emailTextView: TextView = itemView.findViewById(R.id.user_email_textview)
        val checkbox: CheckBox = itemView.findViewById(R.id.user_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.nameTextView.text = user.nombre
        holder.emailTextView.text = user.correo
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            user.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = users.size

    // Método para obtener los usuarios seleccionados
    fun getSelectedUsers(): List<User> = users.filter { it.isSelected!! }
}
