package mx.tecnm.tepic.u4_p1_autocontestadora

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


private var ringgingState = false

class CallReceiver : BroadcastReceiver(){
    var db = FirebaseFirestore.getInstance()
    var num = ArrayList<String>()
    var numno = ArrayList<String>()
    var bueno = " "
    var malo = " "

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent!!.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING)
            ringgingState = true

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK)
            ringgingState = false

        if (ringgingState && intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_IDLE) {
            val number = intent.extras?.getString("NUMERO ENTRANTE")

           mensaje()
            db.collection("LLAMADAS").addSnapshotListener { querySnapshot, i ->
                if (i != null) {
                    return@addSnapshotListener
                }
                num.clear()
                numno.clear()
                for (doc in querySnapshot!!) {
                    val cad = doc.get("TELEFONO")
                    if (doc.getBoolean("DESEADO") == true)
                        num.add(cad.toString())
                    else numno.add(cad.toString())
                }

                if (num.contains(number)) {
                    val sms = SmsManager.getDefault()
                    sms.sendTextMessage(number, null, bueno, null, null)
                    intent.extras?.getString("LLAMADA ENTRANTE")?.let { showToastMsg(context!!, it) }
                }
                if (numno.contains(number)) {
                    val sms = SmsManager.getDefault()
                    sms.sendTextMessage(number, null, malo, null, null)
                    intent.extras?.getString("LLAMADA ENTRANTE")?.let { showToastMsg(context!!, it) }
                }
            }
        }
    }


    private fun mensaje() {
        
        db.collection("MENSAJE").addSnapshotListener { querySnapshot, i ->
            if (i != null) {
                return@addSnapshotListener
            }
            querySnapshot!!.documents
            for (doc in querySnapshot) {
                malo = "${doc.getString("MENSAJE NO DESEADO")}"
                bueno = "${doc.getString("MENSAJE DESEADO")}"
            }
        }
    }

    fun showToastMsg(c: Context, msg: String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_LONG)
        toast.show()
    }

}