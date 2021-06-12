package layout

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL("CREATE TABLE MENSAJE(IDMENSAJE INTEGER PRIMARY KEY AUTOINCREMENT,DESEADO VARCHAR(200),NODESEADO VARCHAR(200))")
        val datos = ContentValues()
        datos.put("IDMENSAJE", 1)
        datos.put("DESEADO", "")
        datos.put("NODESEADO", "")

       bd.insert("MENSAJE", null, datos)
    }

    override fun onUpgrade(bd: SQLiteDatabase, p1: Int, p2: Int) {
    }
}