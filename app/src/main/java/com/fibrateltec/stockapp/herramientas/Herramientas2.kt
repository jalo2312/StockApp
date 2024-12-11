package com.fibrateltec.stockapp.herramientas


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fibrateltec.stockapp.R
import com.fibrateltec.stockapp.empleados.DatePickerFragment
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Herramientas2 : AppCompatActivity() {

    private lateinit var codigo: EditText
    private lateinit var descripcion: EditText
    private lateinit var estado2: EditText
    private lateinit var entrega: EditText
    private lateinit var devolucion: EditText
    private lateinit var observacion: EditText
    private lateinit var agregar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var herramientasAdapter: HerramientasAdapter
    private lateinit var herramientasList: MutableList<HerramientasAdapter.Herramienta>
    private lateinit var spinner: Spinner
    private lateinit var adapter: ArrayAdapter<String> // Adaptador del Spinner
    private lateinit var scroll : ScrollView
    private lateinit var actualizar : Button
    private lateinit var expedicionText: EditText
    private lateinit var vencimientoText: EditText
    private lateinit var pdf : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_herramientas)

        val cedulaEmpleado = intent.getStringExtra("CEDULA_EMPLEADO")
        val btnRefresh = findViewById<ImageView>(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            // Vuelve a cargar los datos desde PHP y notifica al adaptador
            cargarDatosDesdePHP(cedulaEmpleado)
        }

        codigo = findViewById(R.id.codigo) as EditText

        descripcion = findViewById(R.id.descripcion) as EditText

        estado2 = findViewById(R.id.estadoher) as EditText

        entrega = findViewById(R.id.entrega) as EditText
        entrega.setOnClickListener {
            mostrarDatePickerDialog(supportFragmentManager, entrega)
        }
        devolucion = findViewById(R.id.devolucion) as EditText
        devolucion.setOnClickListener {
            mostrarDatePickerDialog2(supportFragmentManager, devolucion)
        }
        expedicionText = findViewById(R.id.expedicionText) as EditText
        expedicionText.setOnClickListener {
            mostrarDatePickerDialog3(supportFragmentManager, expedicionText)
        }

        vencimientoText = findViewById(R.id.vencimientoText) as EditText
        vencimientoText.setOnClickListener {
            mostrarDatePickerDialog4(supportFragmentManager, vencimientoText)
        }

        observacion = findViewById(R.id.observacion) as EditText

        agregar = findViewById(R.id.agregar) as Button

        pdf = findViewById(R.id.pdf) as Button

        recyclerView = findViewById(R.id.listas)
        herramientasList = mutableListOf()
        herramientasAdapter = HerramientasAdapter(herramientasList)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = herramientasAdapter

        // Configurar el adaptador del spinner
        spinner = findViewById(R.id.cedulas) as Spinner
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        obtenerCedulas()
        scroll = findViewById(R.id.scroll)

        val codigoExtra = intent.getStringExtra("codigo")
        val descripcionExtra = intent.getStringExtra("descripcion")
        val estadoExtra = intent.getStringExtra("estado")
        val entregaExtra = intent.getStringExtra("entrega")
        val devolucionExtra = intent.getStringExtra("devolucion")
        val observacionExtra = intent.getStringExtra("observacion")
        val expedicionExtra = intent.getStringExtra("expedicion")
        val vencimientoExtra = intent.getStringExtra("vencimiento")

        // Luego, establece los datos en los campos correspondientes
        codigo.setText(codigoExtra)
        descripcion.setText(descripcionExtra)
        estado2.setText(estadoExtra)
        entrega.setText(entregaExtra)
        devolucion.setText(devolucionExtra)
        observacion.setText(observacionExtra)
        expedicionText.setText(expedicionExtra)
        vencimientoText.setText(vencimientoExtra)

        // Agregar listener para el botón agregar
        agregar.setOnClickListener {

            if (spinner.selectedItem != null) { // Verificar si hay un elemento seleccionado en el spinner
                val cedulaSeleccionado =
                    spinner.selectedItem.toString() // Obtener ID de usuario seleccionado en el spinner
                ejecutarServicioInsertar(
                    "http://192.168.1.38/conexion/insertar_datos.php",
                    cedulaSeleccionado
                )

            } else {
                Toast.makeText(applicationContext, "Spinner vacío", Toast.LENGTH_SHORT).show()
            }
        }

        // Llamar método para cargar datos desde PHP
        cargarDatosDesdePHP(cedulaEmpleado)

        actualizar = findViewById(R.id.actualizar) as Button

        actualizar.setOnClickListener {
            val codigoActualizado = codigo.text.toString().trim()
            val descripcionActualizada = descripcion.text.toString().trim()
            val estadoActualizado = estado2.text.toString().trim()
            val entregaActualizada = entrega.text.toString().trim()
            val devolucionActualizada = devolucion.text.toString().trim()
            val observacionActualizada = observacion.text.toString().trim()
            val expedicionActualizada = expedicionText.text.toString().trim()
            val vencimientoActualizada = vencimientoText.text.toString().trim()


            // Validación para asegurarse de que todos los campos estén llenos antes de actualizar
            if (codigoActualizado.isEmpty() || descripcionActualizada.isEmpty() || estadoActualizado.isEmpty() ||
                entregaActualizada.isEmpty() || devolucionActualizada.isEmpty() || observacionActualizada.isEmpty()||
                expedicionActualizada.isEmpty() || vencimientoActualizada.isEmpty()
            ) {
                Toast.makeText(
                    applicationContext,
                    "Todos los campos son obligatorios para actualizar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val cedulaSeleccionado2 =
                    spinner.selectedItem.toString()
                ejecutarServicioActualizar(
                    "http://192.168.1.38/conexion/actualizar_herramienta.php",
                    codigoActualizado,
                    descripcionActualizada,
                    estadoActualizado,
                    entregaActualizada,
                    devolucionActualizada,
                    observacionActualizada,
                    expedicionActualizada,
                    vencimientoActualizada,
                    cedulaSeleccionado2
                )
                val intent = Intent().apply {
                    putExtra("codigo", codigoActualizado)
                    putExtra("descripcion", descripcionActualizada)
                    putExtra("estado", estadoActualizado)
                    putExtra("entrega", entregaActualizada)
                    putExtra("devolucion", devolucionActualizada)
                    putExtra("observacion", observacionActualizada)
                    putExtra("expedicion", expedicionActualizada)
                    putExtra("vencimiento", vencimientoActualizada)
                }
                setResult(Activity.RESULT_OK, intent)

                val posicion = herramientasList.indexOfFirst { it.codigo == codigoActualizado }

                if (posicion != -1) {
                    // Actualizar los datos de la herramienta en la lista
                    val herramientaActualizada = HerramientasAdapter.Herramienta(
                        codigoActualizado,
                        descripcionActualizada,
                        estadoActualizado,
                        entregaActualizada,
                        devolucionActualizada,
                        observacionActualizada,
                        expedicionActualizada,
                        vencimientoActualizada,
                        cedulaSeleccionado2
                    )
                    herramientasList[posicion] = herramientaActualizada
                    herramientasAdapter.notifyItemChanged(posicion)
                } else {
                    // Manejar el caso donde no se encuentra la herramienta en la lista
                    mostrarError("No se pudo encontrar la herramienta en la lista")
                }

                // Aquí puedes realizar cualquier otra acción adicional después de la actualización
                // ...

                // Finalmente, cierra la actividad actual
                finish()

            }
        }
        pdf.setOnClickListener{
            exportToPDF()
        }

        herramientasAdapter.setOnActualizarClickListener { position ->

            scroll.post {
                scroll.smoothScrollTo(0, 0)
            }
        }

        herramientasAdapter.setOnEliminarClickListener { position ->
            herramientasList.removeAt(position)
            herramientasAdapter.notifyItemRemoved(position)
        }
    }

    private fun ejecutarServicioActualizar(
        url: String,
        codigo: String,
        descripcion: String,
        estado: String,
        entrega: String,
        devolucion: String,
        observacion: String,
        expedicion : String,
        vencimiento : String,
        cedula: String
    ) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, response, Toast.LENGTH_SHORT).show()
                // Aquí puedes realizar cualquier acción adicional después de una actualización exitosa
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("her_cod", codigo)
                parametros.put("her_descripcion", descripcion)
                parametros.put("her_estado", estado)
                parametros.put("her_fecha_entrega", entrega)
                parametros.put("her_fecha_devolucion", devolucion)
                parametros.put("her_observacion", observacion)
                parametros.put("her_fecha_expedicion", expedicion)
                parametros.put("her_fecha_vencimiento", vencimiento)
                parametros["emple_cedula"] = cedula

                return parametros
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }



    fun mostrarDatePickerDialog(fragmentManager: FragmentManager, entrega: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(day, month, year, entrega)
        }
        datePicker.show(fragmentManager, "datePicker")
    }

    fun mostrarDatePickerDialog2(fragmentManager: FragmentManager, devolucion: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected2(day, month, year, devolucion)
        }
        datePicker.show(fragmentManager, "datePicker")
    }
    fun mostrarDatePickerDialog3(fragmentManager: FragmentManager, expedicionText: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected3(day, month, year, expedicionText)
        }
        datePicker.show(fragmentManager, "datePicker")
    }
    fun mostrarDatePickerDialog4(fragmentManager: FragmentManager, vencimientoText: EditText) {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected4(day, month, year, vencimientoText)
        }
        datePicker.show(fragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int, entrega: EditText) {
        entrega.setText("$day/$month/$year")
    }

    private fun onDateSelected2(day: Int, month: Int, year: Int, devolucion: EditText) {
        devolucion.setText("$day/$month/$year")
    }
    private fun onDateSelected3(day: Int, month: Int, year: Int, expedicionText: EditText) {
        expedicionText.setText("$day/$month/$year")
    }
    private fun onDateSelected4(day: Int, month: Int, year: Int, vencimientoText: EditText) {
        vencimientoText.setText("$day/$month/$year")
    }

    private fun obtenerCedulas() {
        val url =
            "http://192.168.1.38/conexion/verificar_cedulas.php" // URL del archivo PHP para obtener ID de usuarios
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Procesar la respuesta del servidor y agregar los ID de usuarios al adaptador del spinner
                val cedula = response.split(",")
                    .toTypedArray() // Suponiendo que la respuesta del servidor es una cadena separada por comas
                val cedulaLista = cedula.toList()
                adapter.addAll(cedulaLista)
            },
            { error ->
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            })

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun limpiarCampos() {
        codigo.text.clear()
        descripcion.text.clear()
        estado2.text.clear()
        entrega.text.clear()
        devolucion.text.clear()
        observacion.text.clear()
        expedicionText.text.clear()
        vencimientoText.text.clear()
    }


    private fun ejecutarServicioInsertar(url: String, cedula: String) {
        // Obtener los valores de los campos de entrada
        val codigoText = codigo.text.toString().trim()
        val descripcionText = descripcion.text.toString().trim()
        val estadoText = estado2.text.toString().trim()
        val entregaText = entrega.text.toString().trim()
        val devolucionText = devolucion.text.toString().trim()
        val observacionText = observacion.text.toString().trim()
        val expedicionText = expedicionText.text.toString().trim()
        val vencimientoText = vencimientoText.text.toString().trim()

        // Verificar si algún campo está vacío
        if (codigoText.isEmpty() || descripcionText.isEmpty() || estadoText.isEmpty() ||
            entregaText.isEmpty() || devolucionText.isEmpty() || observacionText.isEmpty()||
            expedicionText.isEmpty()|| vencimientoText.isEmpty()
        ) {
            // Mostrar un mensaje de error indicando qué campo está vacío
            Toast.makeText(
                applicationContext,
                "Todos los campos son obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                // Agregar el nuevo elemento a la lista de herramientas y notificar al adaptador
                val nuevaHerramienta = HerramientasAdapter.Herramienta(
                    codigoText,
                    descripcionText,
                    estadoText,
                    entregaText,
                    devolucionText,
                    observacionText,
                    expedicionText,
                    vencimientoText,
                    cedula
                )
                herramientasList.add(nuevaHerramienta)
                herramientasAdapter.notifyDataSetChanged()

                Toast.makeText(applicationContext, "OPERACION EXITOSA", Toast.LENGTH_SHORT)
                    .show()
                limpiarCampos() // Limpiar los campos después de la inserción exitosa
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("her_cod", codigoText)
                parametros.put("her_descripcion", descripcionText)
                parametros.put("her_estado", estadoText)
                parametros.put("her_fecha_entrega", entregaText)
                parametros.put("her_fecha_devolucion", devolucionText)
                parametros.put("her_observacion", observacionText)
                parametros.put("her_fecha_expedicion", expedicionText)
                parametros.put("her_fecha_vencimiento", vencimientoText)
                parametros["emple_cedula"] = cedula // Pasar el ID de usuario seleccionado

                return parametros
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }


    private fun cargarDatosDesdePHP(cedulaEmpleado: String?) {
        val url = "http://192.168.1.38/conexion/lista_her.php?cedula=$cedulaEmpleado"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val herramientasEmpleado = mutableListOf<HerramientasAdapter.Herramienta>()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val empleadoCedula = jsonObject.optString("emple_cedula")
                        if (empleadoCedula == cedulaEmpleado) {
                            val herramienta = HerramientasAdapter.Herramienta(
                                jsonObject.optString("her_cod"),
                                jsonObject.optString("her_descripcion"),
                                jsonObject.optString("her_estado"),
                                jsonObject.optString("her_fecha_entrega"),
                                jsonObject.optString("her_fecha_devolucion"),
                                jsonObject.optString("her_observacion"),
                                jsonObject.optString("her_fecha_expedicion"),
                                jsonObject.optString("her_fecha_vencimiento"),
                                empleadoCedula
                            )
                            herramientasEmpleado.add(herramienta)
                        }
                    }
                    herramientasList.clear()
                    herramientasList.addAll(herramientasEmpleado)
                    herramientasAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    mostrarError("Error al procesar los datos JSON")
                    e.printStackTrace()
                }
            },
            { error ->
                mostrarError("Error en la solicitud: ${error.message}")
            })

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun exportToPDF() {
        val document = Document()
        val currentDateAndTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Registro_$currentDateAndTime.pdf"
        val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName).absolutePath
        val file = File(path)
        try {
            val fileOutputStream = FileOutputStream(file)

            PdfWriter.getInstance(document, fileOutputStream)

            document.open()

            val headerTable = PdfPTable(1)
            headerTable.widthPercentage = 100f
            headerTable.spacingBefore = 10f

            // Agregar texto en la celda de la tabla del encabezado
            val headerCell = PdfPCell(Phrase("Herramientas Registradas"))
            headerCell.horizontalAlignment = Element.ALIGN_CENTER
            headerCell.backgroundColor = BaseColor.LIGHT_GRAY
            headerTable.addCell(headerCell)

            // Agrega la tabla del encabezado al documento
            document.add(headerTable)

            // Crear una tabla con 8 columnas
            val table = PdfPTable(9)
            table.widthPercentage = 100f
            table.spacingBefore = 5f

            // Agregar texto en cada celda de la tabla
            val cell1 = PdfPCell(Phrase("Código"))
            val cell2 = PdfPCell(Phrase("Descripción"))
            val cell3 = PdfPCell(Phrase("Estado"))
            val cell4 = PdfPCell(Phrase("Entrega"))
            val cell5 = PdfPCell(Phrase("Devolución"))
            val cell6 = PdfPCell(Phrase("Observación"))
            val cell7 = PdfPCell(Phrase("Expedición"))
            val cell8 = PdfPCell(Phrase("Vencimiento"))
            val cell9 = PdfPCell(Phrase("Cedula"))

            // Ajustar la alineación de las celdas
            cell1.horizontalAlignment = Element.ALIGN_CENTER
            cell2.horizontalAlignment = Element.ALIGN_CENTER
            cell3.horizontalAlignment = Element.ALIGN_CENTER
            cell4.horizontalAlignment = Element.ALIGN_CENTER
            cell5.horizontalAlignment = Element.ALIGN_CENTER
            cell6.horizontalAlignment = Element.ALIGN_CENTER
            cell7.horizontalAlignment = Element.ALIGN_CENTER
            cell8.horizontalAlignment = Element.ALIGN_CENTER
            cell9.horizontalAlignment = Element.ALIGN_CENTER

            // Agregar las celdas a la tabla
            table.addCell(cell1)
            table.addCell(cell2)
            table.addCell(cell3)
            table.addCell(cell4)
            table.addCell(cell5)
            table.addCell(cell6)
            table.addCell(cell7)
            table.addCell(cell8)
            table.addCell(cell9)

            // Agrega la tabla al documento
            document.add(table)

            // Agrega el contenido principal al documento
            val constraint: RecyclerView = findViewById(R.id.listas)
            addViewToPDF(document, constraint)

            Toast.makeText(this, "Guardado exitosamente en $path", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }




    private fun addViewToPDF(document: Document, view: View) {

        // Calcula el margen del documento
        val margin = 0f

        // Calcula el tamaño de la página del documento
        val increasedPageWidth = 550f
        // Calcula el tamaño de la página del documento
        val pageSize = document.pageSize
        val pageWidth = increasedPageWidth - margin * 2.5f
        val pageHeight = pageSize.height - margin * 2.5f

        // Convierte la vista a un bitmap
        val bitmap = convertViewToBitmap(view)

        // Convierte el bitmap a bytes para agregarlo al documento PDF
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image.getInstance(stream.toByteArray())

        // Ajusta el tamaño de la imagen al documento
        val aspectRatio = image.width.toFloat() / image.height.toFloat()
        val newWidth = pageWidth * 1f // Ajusta el ancho según tu preferencia
        val newHeight = newWidth / aspectRatio

        // Si la imagen es más grande que la página, divide la imagen en varias partes
        if (newHeight > pageHeight) {
            divideBitmapIntoSections(document, bitmap, pageHeight, aspectRatio, pageWidth)
        } else {
            // Ajusta el tamaño de la imagen al documento
            image.scaleToFit(newWidth, newHeight)

            // Agrega la imagen al documento
            document.add(image)
        }
    }

    private fun convertViewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun divideBitmapIntoSections(document: Document, bitmap: Bitmap, pageHeight: Float, aspectRatio: Float, pageWidth: Float) {
        val numVerticalSections = Math.ceil((bitmap.height.toDouble() / pageHeight)).toInt()
        var startY = 0f

        for (i in 0 until numVerticalSections) {
            var sectionHeight = pageHeight
            val remainingHeight = bitmap.height - startY

            if (remainingHeight < sectionHeight) {
                sectionHeight = remainingHeight
            }

            val sectionBitmap = Bitmap.createBitmap(bitmap, 0, startY.toInt(), bitmap.width, sectionHeight.toInt())

            val byteArrayOutputStream = ByteArrayOutputStream()
            sectionBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val sectionImage = Image.getInstance(byteArray)
            sectionImage.scaleToFit(pageWidth, sectionHeight)

            document.newPage()
            document.add(sectionImage)

            startY += sectionHeight
        }
    }

}


