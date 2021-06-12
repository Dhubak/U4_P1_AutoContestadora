package mx.tecnm.tepic.u4_p1_autocontestadora

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.u4_p1_autocontestadora.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    var db = FirebaseFirestore.getInstance()
    private var datosArray = ArrayList<String>()
    private var idArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val vista= binding.root
        setContentView(vista)

        cont()

        binding.button4.setOnClickListener {
            if(textos())return@setOnClickListener
            val data = hashMapOf("TELEFONO" to binding.tel.text.toString().toLong(), "NOMBRE" to binding.nom.text.toString(), "DESEADO" to binding.rb.isChecked
            )
            db.collection("LLAMADAS_CONTESTADAS").add(data as Any)
            clear()
            cont()
        }
        binding.button5.setOnClickListener {
            finish()
        }
    }

    private fun textos(): Boolean {
        if (binding.nom.text.toString().trim() == "") {
            binding.nom.error = "INGRESA UN NOMBRE DE CONTACTO"
            return true
        }
        if (binding.tel.text.toString().trim() == "") {
            binding.tel.error = "INGRESA UN NUMERO TELEFONICO"
            return true

        }

        return false
    }

    private fun cont() {

        db.collection("LLAMADAS_CONTESTADAS").addSnapshotListener { querySnapshot, i ->
            if (i != null) {
                return@addSnapshotListener
            }
            datosArray.clear()
            idArray.clear()

            querySnapshot!!.documents

            for (doc in querySnapshot) {
                val cad = "NOMBRE: ${doc.getString("NOMBRE")}\n" +
                        "TELEFONO: ${doc.get("TELEFONO")}\n" +
                        "¿DESEADO?: ${if(doc.getBoolean("DESEADA") == true) "SI" else "NO"}\n"
                datosArray.add(cad)
                idArray.add(doc.id)
            }
            if (datosArray.isEmpty()) datosArray.add("ERROR! NO HAY CONTACTOS")
            binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, datosArray)
            this.registerForContextMenu(binding.lista)
            binding.lista.setOnItemClickListener { _, _, i, _ ->
                trans(i)
            }
        }
    }

    private fun trans(index: Int) {
        val id = this.idArray[index]

        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage("¿QUE DESEA REALIZAR CON LA INFORMACION: \n ${datosArray[index]}?")
            .setPositiveButton("CANCELAR") { _, _ -> }
            .setNegativeButton("ELIMINAR") { _, _ ->
                delete(id)
            }
            .show()
    }

    private fun delete(id: String) {
        db.collection("LLAMADAS_CONTESTADAS").document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "EL REGISTRO A SIDO BORRADO EXITOSAMENTE",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "ERROR! ALGO A SALIDO MAL...", Toast.LENGTH_LONG).show()
            }
    }

    private fun clear() {
        binding.nom.setText("")
        binding.tel.setText("")
    }
}