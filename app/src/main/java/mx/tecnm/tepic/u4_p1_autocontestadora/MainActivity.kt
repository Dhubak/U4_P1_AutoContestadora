package mx.tecnm.tepic.u4_p1_autocontestadora

import android.Manifest
import android.R
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import layout.BaseDatos
import mx.tecnm.tepic.u4_p1_autocontestadora.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var listCalls =listOf<String>(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE).toTypedArray()
    private var datosArray = ArrayList<String>()
    var dbb=BaseDatos(this, "MENSAJE", null, 1)
    var db = FirebaseFirestore.getInstance()
    var numero = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val vista= binding.root
        setContentView(vista)
        checarpermiso()
        tomarnumero()

        binding.button.setOnClickListener {
            guardarmensaje()
        }

        binding.button1.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            finish()
        }

    }

    private fun guardarmensaje() {
        val dat = hashMapOf("DESEADO" to binding.men1.text.toString(), "NO DESEADO" to binding.men2.text.toString()
        )
        db.collection("MENSAJES")
            .add(dat)
            .addOnSuccessListener {
                alerta("EXITO! SE INSERTO CORRECTAMENTE")
                men1.setText("")
                men2.setText("")
            }
            .addOnFailureListener {
                mensaje("ERROR! NO SE PUDO INSERTAR")
            }


    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){_,_->}
            .show()
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) transfer()
    }

    private fun tomarnumero() {
        try {
            db.collection("LLAMADAS_CONTESTADAS").addSnapshotListener { querySnapshot, i ->
                if (i != null) {
                    return@addSnapshotListener
                }
                for (doc in querySnapshot!!) {
                    val cad = doc.get("TELEFONO")
                    numero.add(cad.toString())
                }
            }
        } catch (e: Exception) {
            Log.w("ERROR", e.message!!)
        }
    }

    private fun checarpermiso() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                369
            )
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            this,
            Array(1) { Manifest.permission.READ_CALL_LOG }, 101
        ) else transfer()
    }

    private fun transfer() {
        val transR = dbb.readableDatabase
        try {
            val cursor = transR.query("MENSAJE", arrayOf("*"), "IDMENSAJE=?", arrayOf("1"), null, null, null)
            if (cursor.moveToFirst()) {
                binding.men1.setText(cursor.getString(1))
                binding.men2.setText(cursor.getString(2))
            }
        } catch (e: SQLiteException) {

        } finally {
            transR.close()
        }
    }

}