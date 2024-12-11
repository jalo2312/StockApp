package com.fibrateltec.stockapp.lista

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fibrateltec.stockapp.R

class EmpleadosLista : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmpleadoAdapter
    private var empleadosList = mutableListOf<Empleado>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_lista)

        recyclerView = findViewById(R.id.lista_elementos)
        adapter= EmpleadoAdapter(empleadosList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Llamar a la función para cargar los empleados desde la base de datos
        cargarEmpleados()
    }
    private fun cargarEmpleados() {
        val url = "http://192.168.1.38/conexion/empleados_lista.php" // URL del archivo PHP que devuelve los empleados
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Procesar la respuesta del servidor y agregar los empleados a la lista
                val empleadosArray = response.split("<br>") // Ahora se separan por saltos de línea
                for (empleadoStr in empleadosArray) {
                    val empleadoInfo = empleadoStr.split("-")
                    if (empleadoInfo.size == 2) { // Ahora solo hay dos partes: cédula y nombre
                        val empleado = Empleado(empleadoInfo[0], empleadoInfo[1], "", "") // No hay estado ni cargo en la respuesta del servidor
                        empleadosList.add(empleado)
                    }
                }
                adapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
            },
            { error ->
                Toast.makeText(applicationContext, "Error al cargar empleados: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }
    // Función de ejemplo para obtener empleados

}