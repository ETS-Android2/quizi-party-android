package com.grupo14.quiziparty;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBaseSQL extends SQLiteOpenHelper {
    public DataBaseSQL(Context context) {
        super(context, "quiziparty", null, 1);
    }

    protected SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table tests (id integer primary key autoincrement not null, pregunta text, " +
                "respuestas text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table IF EXISTS tests");
    }

    public boolean isEmpty() {
        db = this.getReadableDatabase();
        Cursor cantidad = db.rawQuery("SELECT COUNT(*) FROM tests", null);
        if (cantidad != null) {
            cantidad.moveToFirst();
            if (cantidad.getInt(0) == 0) {
                db.close();
                return true;
            } else {
                db.close();
                return false;
            }
        } else {
            db.close();
            return true;
        }
    }

    public String getLista() {
        String lista = "";
        Cursor res = null;
        String contenido = "";
        db = this.getReadableDatabase();
        res = db.rawQuery("SELECT * FROM tests", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            contenido = res.getString(res.getColumnIndex("pregunta")) + res.getString(res.getColumnIndex("respuestas")) + "\n";
            lista = lista + contenido;
            res.moveToNext();
        }
        db.close();
        return lista;
    }

    public String[] getPregunta(Integer id) {
        String[] lista = new String[3];
        Cursor res = null;
        String contenido = "";
        db = this.getReadableDatabase();
        res = db.rawQuery("SELECT * FROM tests WHERE id = '" + id + "'", null);
        res.moveToFirst();
        contenido = res.getString(res.getColumnIndex("id"));
        lista[0] = contenido;
        contenido = res.getString(res.getColumnIndex("pregunta"));
        lista[1] = contenido;
        contenido = res.getString(res.getColumnIndex("respuestas"));
        lista[2] = contenido;
        db.close();
        return lista;
    }

    public void insertPregunta(String pregunta, String respuestas) {
        db = this.getReadableDatabase();
        db.execSQL("INSERT INTO tests (pregunta, respuestas) VALUES ('" + pregunta + "', '" + respuestas + "')");
        db.close();
    }

    public void updatePregunta(Integer id, String pregunta, String respuestas) {
        db = this.getWritableDatabase();
        db.execSQL("UPDATE tests SET pregunta ='" + pregunta + "', " +
                "respuestas ='" + respuestas + "', " +
                "WHERE id = " + id);
        db.close();
    }

    public void deleteAllPreguntas() {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tests");
        db.execSQL("UPDATE sqlite_sequence SET seq=0 WHERE name = 'tests';");
        db.close();
    }

    public void deletePregunta(Integer id) {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tests WHERE id =" + id);
        db.execSQL("UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM tests) WHERE name = 'tests'");
        db.execSQL("UPDATE sqlite_sequence SET seq=" + id + " - 1 WHERE name = 'tests';");
        db.close();
    }
}