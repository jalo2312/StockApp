package com.fibrateltec.stockapp.lista

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fibrateltec.stockapp.R
import com.fibrateltec.stockapp.herramientas.Herramientas2

// Define la clase Employee fuera de cualquier otra clase en un archivo separado

    data class Empleado(
        val cedula: String,
        val nombre: String,
        val estado: String,
        val cargo: String,
    )
    class EmpleadoAdapter(private val empleados: List<Empleado>) : RecyclerView.Adapter<EmpleadoAdapter.EmpleadoViewHolder>() {

        private var empleadoSeleccionado: Empleado? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_empleados, parent, false)
            return EmpleadoViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: EmpleadoViewHolder, position: Int) {
            val currentEmpleado = empleados[position]
            holder.bind(currentEmpleado)
            holder.itemView.setOnClickListener {
                // Establecer el empleado seleccionado
                empleadoSeleccionado = currentEmpleado
                // Redirigir a la actividad Herramientas con los detalles del empleado seleccionado
                val context = holder.itemView.context
                val intent = Intent(context, Herramientas2::class.java).apply {
                    putExtra("CEDULA_EMPLEADO", currentEmpleado.cedula)
                    // Puedes agregar más detalles del empleado aquí si es necesario
                }
                context.startActivity(intent)
            }
        }

        override fun getItemCount() = empleados.size

        inner class EmpleadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nombreTextView: TextView = itemView.findViewById(R.id.nombre_emple)
            private val cedulaTextView: TextView = itemView.findViewById(R.id.cedula_emple)

            fun bind(empleado: Empleado) {
                nombreTextView.text = empleado.nombre
                cedulaTextView.text = empleado.cedula

                // Agregar OnClickListener al nombre del empleado
                nombreTextView.setOnClickListener {
                    navigateToHerramientas(empleado)
                }

                // Agregar OnClickListener a la cédula del empleado
                cedulaTextView.setOnClickListener {
                    navigateToHerramientas(empleado)
                }
            }

            private fun navigateToHerramientas(empleado: Empleado) {
                // Redirigir a la actividad Herramientas con los detalles del empleado seleccionado
                val context = itemView.context
                val intent = Intent(context, Herramientas2::class.java).apply {
                    putExtra("CEDULA_EMPLEADO", empleado.cedula)
                    // Puedes agregar más detalles del empleado aquí si es necesario
                }
                context.startActivity(intent)
            }
        }

    }
