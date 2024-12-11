package com.fibrateltec.stockapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fibrateltec.stockapp.empleados.DatePickerFragment
import com.fibrateltec.stockapp.lista.Empleado
import com.fibrateltec.stockapp.lista.EmpleadoAdapter
import com.fibrateltec.stockapp.lista.EmpleadosLista
object EmpleadosManager {
    var empleadosList = mutableListOf<Empleado>()
    lateinit var adapter1: EmpleadoAdapter
}

class MainActivity2 : AppCompatActivity() {

    private lateinit var cedula: EditText
    private lateinit var nombre: EditText
    private lateinit var estado: EditText
    private lateinit var cargo: EditText

    private lateinit var guardar: Button
    private lateinit var verLista : Button
    private lateinit var adapter: ArrayAdapter<String> // Adaptador del Spinner
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        cedula = findViewById(R.id.cedula) as EditText
        nombre = findViewById(R.id.nombre) as EditText
        estado = findViewById(R.id.estado) as EditText
        cargo = findViewById(R.id.cargo) as EditText

        guardar = findViewById(R.id.guardar) as Button
        verLista = findViewById(R.id.ver) as Button


        spinner = findViewById(R.id.id_users) as Spinner
        // Configurar el adaptador del spinner
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Llamar a la función para obtener los ID de usuarios desde el servidor
        obtenerIdUsuarios()
        EmpleadosManager.adapter1 = EmpleadoAdapter(EmpleadosManager.empleadosList)

        // Asignar el listener al botón guardar
        guardar.setOnClickListener {
            if (spinner.selectedItem != null) { // Verificar si hay un elemento seleccionado en el spinner
                val idUsuarioSeleccionado =
                    spinner.selectedItem.toString() // Obtener ID de usuario seleccionado en el spinner
                // Verificar si algún campo está vacío
                if (cedula.text.toString().trim().isEmpty() || nombre.text.toString().trim().isEmpty() ||
                    estado.text.toString().trim().isEmpty() || cargo.text.toString().trim().isEmpty()
                ) {
                    // Mostrar un mensaje de error indicando qué campo está vacío
                    Toast.makeText(applicationContext, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                } else {
                    ejecutarServicio(
                        "http://192.168.1.38/conexion/insertar_empleados.php",
                        idUsuarioSeleccionado
                    )
                    val nuevoEmpleado = Empleado(
                        cedula.text.toString(),
                        nombre.text.toString(),
                        estado.text.toString(),
                        cargo.text.toString()
                    )
                    EmpleadosManager.empleadosList.add(nuevoEmpleado)

                    // Notificar al adaptador de cambios en los datos
                    EmpleadosManager.adapter1.notifyDataSetChanged()

                    val intent = Intent(applicationContext, EmpleadosLista::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(applicationContext, "Spinner vacío", Toast.LENGTH_SHORT).show()
            }
        }
        verLista.setOnClickListener{
            val intent = Intent(applicationContext, EmpleadosLista::class.java)
            startActivity(intent)
        }

    }



    private fun obtenerIdUsuarios() {
        val url =
            "http://192.168.1.38/conexion/verificar.php" // URL del archivo PHP para obtener ID de usuarios
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Procesar la respuesta del servidor y agregar los ID de usuarios al adaptador del spinner
                val idUsuarios = response.split(",")
                    .toTypedArray() // Suponiendo que la respuesta del servidor es una cadena separada por comas
                val idUsuariosLista = idUsuarios.toList()
                adapter.addAll(idUsuariosLista)
            },
            { error ->
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            })

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }
    private fun limpiarCampos() {
        cedula.text.clear()
        nombre.text.clear()
        estado.text.clear()
        cargo.text.clear()

    }


    private fun ejecutarServicio(url: String, idUsuario: String) {
        // Obtener el texto de los campos y eliminar espacios en blanco
        val cedulaText = cedula.text.toString().trim()
        val nombreText = nombre.text.toString().trim()
        val estadoText = estado.text.toString().trim()
        val cargoText = cargo.text.toString().trim()

        // Verificar si algún campo está vacío
        if (cedulaText.isEmpty() || nombreText.isEmpty() || estadoText.isEmpty() || cargoText.isEmpty()) {
            // Mostrar un mensaje de error indicando qué campo está vacío
            Toast.makeText(applicationContext, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Si no hay campos vacíos, enviar la solicitud al servidor
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(applicationContext, "OPERACION EXITOSA", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            },
            { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros["emple_cedula"] = cedulaText
                parametros["emple_nombre"] = nombreText
                parametros["emple_estado"] = estadoText
                parametros["emple_cargo"] = cargoText
                parametros["usu_id"] = idUsuario // Pasar el ID de usuario seleccionado
                return parametros
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }


}
