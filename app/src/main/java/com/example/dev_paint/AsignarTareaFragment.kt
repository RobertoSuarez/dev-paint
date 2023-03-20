package com.example.dev_paint

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dev_paint.adaptadores.UserAdapter
import com.example.dev_paint.models.Tarea
import com.example.dev_paint.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AsignarTareaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AsignarTareaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var PICK_IMAGE_REQUEST = 200
    private var PERMISSION_REQUEST_CODE = 300

    private lateinit var tareaImageView : ImageView
    private lateinit var cargarButton : Button
    private lateinit var asignarButton : Button
    private var imageUp: Uri? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_asignar_tarea, container, false)

        tareaImageView = view.findViewById<ImageView>(R.id.img_tarea)
        cargarButton = view.findViewById<Button>(R.id.btn_cargar_img)
        asignarButton = view.findViewById<Button>(R.id.btn_asignar)

        cargarButton.setOnClickListener {
            subirImagen(view)
        }

        recyclerView = view.findViewById(R.id.lista_usuarios)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Insertamos los estudiantes al recycler view.
        User.getUsersByRole("Estudiante") { estudiantes ->
            adapter = UserAdapter(estudiantes!!)
            recyclerView.adapter = adapter
        }



        asignarButton.setOnClickListener {
            println("Asignar")

            if (imageUp == null) {
                Toast.makeText(view.context, "Se requiera que suba una imagen", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            uploadImageToFirebaseStorage(imageUp!!,"tareas", generateFileName()) { imageUrl, error ->
                if (error != null) {
                    println("Un tremendo error")
                } else {
                    User.getCurrentUser { user ->

                        // obtenemos todos los estudiantes selecionados
                        getSelectedUsers().forEach { estudiante ->
                            val tarea = Tarea(
                                urlImgen = imageUrl.toString(),
                                urlImgenEstudiante = "",
                                uidEstudiante = estudiante.id!!,
                                nombreEstudiante = estudiante.nombre!!,
                                uidProfesor = user?.id.toString(),
                                nombreProfesor = user?.nombre.toString(),
                                estado = "Pentiente",
                                fecha = Date(),
                                calificacion = 0
                            )
                            // enviamos el documento a firestore
                            tarea.guardarTarea()
                        }



                        Toast.makeText(view.context, "Tarea Asignada correctamente", Toast.LENGTH_LONG).show()
                    }


                }

            }


        }


        return view
    }

    fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "image_$timestamp.jpg"
    }

    // Método para obtener los usuarios seleccionados
    fun getSelectedUsers(): List<User> = adapter.getSelectedUsers()

    fun uploadImageToFirebaseStorage(uri: Uri, folderName: String, fileName: String, onComplete: (imageUrl: String?, error: String?) -> Unit) {

        // Create a reference to the Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference

        // Create a reference to the folder where the image will be stored
        val folderRef = storageRef.child(folderName)

        // Create a reference to the file where the image will be stored
        val fileRef = folderRef.child(fileName)

        // Upload the file to Firebase Storage
        val uploadTask: UploadTask = fileRef.putFile(uri)

        // Add a listener to get the result of the upload
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onComplete(downloadUri.toString(), null)
            } else {
                onComplete(null, task.exception?.message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            imageUp = imageUri
            tareaImageView.setImageURI(imageUri)
        }
    }

    private fun subirImagen(view: View) {
        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(view.context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            // El permiso ya ha sido concedido
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AsignarTareaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AsignarTareaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}