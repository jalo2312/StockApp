package com.fibrateltec.stockapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    private lateinit var btnUsuario: EditText
    private lateinit var btnContraseña: EditText
    private lateinit var iniciar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUsuario = findViewById(R.id.btnusuario)
        btnContraseña = findViewById(R.id.btncontraseña)
        iniciar = findViewById(R.id.iniciar)

        iniciar.setOnClickListener {
            val usuario = btnUsuario.text.toString().trim()
            val contraseña = btnContraseña.text.toString().trim()

            if (usuario.isNotEmpty() && contraseña.isNotEmpty()) {
                validarCredenciales(usuario, contraseña)
            } else {
                Toast.makeText(this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCredenciales(usuario: String, contraseña: String) {
        val url = "http://192.168.1.38/conexion/validar_usuario.php"
        val request = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                // Manejar la respuesta del servidor
                if (response.isNotEmpty()) {
                    // El servidor retornó datos, se considera que las credenciales son válidas
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Aquí puedes realizar otras acciones, como redirigir a otra actividad
                    val intent = Intent(applicationContext, MainActivity2::class.java)
                    startActivity(intent)
                } else {
                    // El servidor no retornó datos, se considera que las credenciales son inválidas
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = usuario
                params["clave"] = contraseña
                return params
            }
        }

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request)
    }
}