package com.example.dev_paint

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.maxkeppeler.sheets.color.ColorSheet
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.text.DecimalFormat
import android.Manifest
import android.graphics.Color
import com.example.dev_paint.models.Calificacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImitarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImitarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var paintView: PaintView
    private lateinit var btnColor : Button
    private lateinit var seekBar : SeekBar
    private lateinit var imageView : ImageView
    private var imageUp: Uri? = null
    private lateinit var toolbar : MaterialToolbar
    private var PICK_IMAGE_REQUEST = 1
    private var PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                this.imageUp = imageUri
            }
            imageView.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso ha sido concedido
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            } else {
                // El permiso ha sido denegado
                Toast.makeText(context, "Debe conceder permiso para comparar las imagenes", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_imitar, container, false)

        paintView = view.findViewById(R.id.paintView)
        btnColor = view.findViewById<Button>(R.id.btn_select_color)
        seekBar = view.findViewById<SeekBar>(R.id.brushSize)
        imageView = view.findViewById<ImageView>(R.id.imgen_imitar)
        seekBar.progress = 10

        btnColor.setBackgroundColor(Color.BLACK)
        btnColor.setOnClickListener {
            println("Seleccionar un color")
            ColorSheet().show(view.context) {
                title("Selecciona un color")
                onPositive { color ->
                    println(color)
                    paintView.paint.color = color
                    btnColor.setBackgroundColor(color)
                }
            }
        }

        toolbar = view.findViewById(R.id.appbarDibujar)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                // Sube las imagenes a firebase y las compara con la API
                R.id.action_compare -> {
                    this.captureAndSaveLowerHalfToFirebase(view.context)
                    true
                }
                // seleciona una imagen de la galeria
                R.id.action_upload -> {
                    // action para subir imagen a la app
                    this.uploadCompareImage(view)
                    true
                }
                // Limpia el lienso de la app
                R.id.action_clean -> {
                    paintView.clear()
                    true
                }
                // guarda la imagen en Galeria
                R.id.action_guardar -> {
                    println("Guardar")
                    paintView.captureScreenAndSaveToGallery()
                    true
                }
                else -> false
            }
        }


        // Establecer un OnSeekBarChangeListener en el SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0 // Variable global para almacenar el valor de progreso del SeekBar

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actualizar la variable global con el nuevo valor de progreso
                this.progress = progress
                println(progress)
                //paintView.paint.strokeWidth = progress.toFloat()
                paintView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No es necesario implementar este método para obtener el valor del SeekBar cada vez que cambie
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No es necesario implementar este método para obtener el valor del SeekBar cada vez que cambie
            }
        })

        // Inflate the layout for this fragment
        return view
    }

    fun captureAndSaveLowerHalfToFirebase(context: Context) {

        // verficamos si el usuario subio una imagen a la app
        if (imageUp == null) {
            Toast.makeText(context, "No se ha seleccionado ninguna imagen", Toast.LENGTH_LONG).show()
            return
        }

        // Obtiene el tamaño de la pantalla
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        // Crea un objeto Bitmap del tamaño de la pantalla
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Crea un objeto Canvas para dibujar en el Bitmap
        val canvas = Canvas(bitmap)

        // Dibuja la pantalla en el Bitmap
        val rootView = (context as Activity).window.decorView
        rootView.draw(canvas)

        // Crea un nuevo Bitmap con la parte inferior de la mitad de la pantalla
        val croppedBitmap = Bitmap.createBitmap(bitmap, 0, height/2, width, (height/2) - 150 )

        // Guarda el Bitmap en la galería
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream?.close()
        }

        // Sube la imagen a Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${generateFileName()}.jpg")

        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            imagesRef.putFile(uri).addOnSuccessListener {
                Toast.makeText(context, "Imagen subida a Firebase Storage", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al subir imagen a Firebase Storage", Toast.LENGTH_SHORT).show()
            }
        }

        if (uri != null && imageUp != null) {
            uploadImagesToFirebase(uri, imageUp!!)
        }

        Toast.makeText(context, "Se guardó la imagen en la galería", Toast.LENGTH_SHORT).show()
    }

    private fun uploadCompareImage(view: View) {
        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(view.context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            // El permiso ya ha sido concedido
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "image_$timestamp"
    }

    private fun uploadImagesToFirebase(imageUri1: Uri, imageUri2: Uri) {
        // Crear referencia a la raíz del almacenamiento de Firebase
        val storageRef = FirebaseStorage.getInstance().reference

        // Crear referencias para las imágenes a subir
        val imageRef1 = storageRef.child("images/image1.jpg")
        val imageRef2 = storageRef.child("images/image2.jpg")

        // Subir la imagen 1
        imageRef1.putFile(imageUri1)
            .addOnSuccessListener {
                // La imagen 1 se subió correctamente
                // Obtener la URL de descarga de la imagen 1
                imageRef1.downloadUrl.addOnSuccessListener { uri1 ->
                    // Subir la imagen 2
                    imageRef2.putFile(imageUri2)
                        .addOnSuccessListener {
                            // La imagen 2 se subió correctamente
                            // Obtener la URL de descarga de la imagen 2
                            imageRef2.downloadUrl.addOnSuccessListener { uri2 ->
                                // Las dos imágenes se subieron correctamente
                                // Aquí puedes realizar cualquier acción que requieras con las URLs
                                // Por ejemplo, guardar las URLs en una base de datos
                                // o mostrar las imágenes en una vista
                                Log.d(TAG, "URL de la imagen 1: $uri1")
                                Log.d(TAG, "URL de la imagen 2: $uri2")



                                // Crear un scope de coroutine
                                val scope = CoroutineScope(Dispatchers.Main)

                                // Llamar a la función suspendida dentro del scope de la coroutine
                                scope.launch {
                                    val json = JsonObject()
                                    json.add("image_a", JsonObject().apply {
                                        addProperty("type", "url")
                                        addProperty("content", uri1.toString())
                                    })
                                    json.add("image_b", JsonObject().apply {
                                        addProperty("type", "url")
                                        addProperty("content", uri2.toString())
                                    })

                                    val similarity = getSimilarity(json)
                                    val decimalFormat = DecimalFormat("#.###")
                                    decimalFormat.minimumFractionDigits = 3
                                    val formattedNumber = decimalFormat.format(similarity)
                                    println(formattedNumber)
                                    if (similarity != null) {
                                        Toast.makeText(context, "Similitud: " + similarity * 10f, Toast.LENGTH_LONG).show()
                                    }

                                    val auth = FirebaseAuth.getInstance()
                                    val db = FirebaseFirestore.getInstance()
                                    val currentUser = auth.currentUser

                                    if (currentUser != null) {
                                        val userRef = db.collection("usuarios").document(currentUser.uid)
                                        userRef.get()
                                            .addOnSuccessListener {user ->
                                                val nombre = user.getString("nombre")
                                                // registrar la información de la comparación
                                                val calificacion = Calificacion(
                                                    uid = currentUser.uid,
                                                    name = nombre!!,
                                                    img1 = uri1.toString(),
                                                    img2 = uri2.toString(),
                                                    rating = similarity!! * 10,
                                                    fecha = Date()
                                                )

                                                val caliReff = db.collection("calificaciones")
                                                caliReff.add(calificacion)
                                                    .addOnSuccessListener { documentReferen ->
                                                        Toast.makeText(context, "Se registro la calificación en firestore", Toast.LENGTH_LONG).show()
                                                    }.addOnFailureListener {e ->
                                                        println(e.toString())
                                                    }
                                            }
                                    }
                                }
                            }.addOnFailureListener { e ->
                                // Error al obtener la URL de descarga de la imagen 2
                                Log.e(TAG, "Error al obtener la URL de descarga de la imagen 2: $e")
                            }
                        }.addOnFailureListener { e ->
                            // Error al subir la imagen 2
                            Log.e(TAG, "Error al subir la imagen 2: $e")
                        }
                }.addOnFailureListener { e ->
                    // Error al obtener la URL de descarga de la imagen 1
                    Log.e(TAG, "Error al obtener la URL de descarga de la imagen 1: $e")
                }
            }.addOnFailureListener { e ->
                // Error al subir la imagen 1
                Log.e(TAG, "Error al subir la imagen 1: $e")
            }
    }

    interface SimilarityApi {
        @Headers(
            "x-rapidapi-key: 28ca0e4b69mshf3acc3797b85a64p1c1815jsn566733139979",
            "x-rapidapi-host: similarity2.p.rapidapi.com",
            "Content-Type: application/json"
        )
        @POST("/similarity")
        suspend fun getSimilarity(@Body request: JsonObject): SimilarityResponse

        companion object {
            fun create(): SimilarityApi {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://similarity2.p.rapidapi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(SimilarityApi::class.java)
            }
        }
    }

    data class SimilarityResponse(val similarity: Double)

    suspend fun getSimilarity(json: JsonObject): Double? {
        val api = SimilarityApi.create()
        val response = api.getSimilarity(json)
        return response.similarity
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImitarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImitarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }
}