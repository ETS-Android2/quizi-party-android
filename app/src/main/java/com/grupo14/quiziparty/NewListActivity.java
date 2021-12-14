package com.grupo14.quiziparty;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

public class NewListActivity extends AppCompatActivity {

    private EditText etext_question;
    private EditText etext_answer1;
    private EditText etext_answer2;
    private EditText etext_answer3;
    private EditText etext_correct_answer;
    private Button btn_next_question;
    private ImageButton btn_borrar;
    private DataBaseSQL db = new DataBaseSQL(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        setUpView();
    }

    private void setUpView(){
        // Elementos del Layout
        etext_question = findViewById(R.id.etext_question);
        etext_answer1 = findViewById(R.id.etext_answer1);
        etext_answer2 = findViewById(R.id.etext_answer2);
        etext_answer3 = findViewById(R.id.etext_answer3);
        btn_borrar = findViewById(R.id.img_delete);
        etext_correct_answer = findViewById(R.id.etext_correct_answer);
        btn_next_question = findViewById(R.id.btn_next_question);

        // Acción del Botón Siguiente
        btn_next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
                etext_question.getText().clear();
                etext_answer1.getText().clear();
                etext_answer2.getText().clear();
                etext_answer3.getText().clear();
                etext_correct_answer.getText().clear();
            }
        });

        // Acción del Botón Imagen Borrar
        btn_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!db.isEmpty()) { // Si la base no está vacía, avisa para borrarla o no
                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(NewListActivity.this);
                    builder.setMessage(getString(R.string.alert_borrar));
                    builder.setPositiveButton(R.string.alert_aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { // Borrar la lista personalizada
                            db.deleteAllPreguntas();
                            Toast.makeText(NewListActivity.this, getString(R.string.toast_borrado), Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(R.string.alert_cancelar, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else { // Si la base está vacía, avisa de ello
                    Toast.makeText(NewListActivity.this, getString(R.string.toast_lista_vacia), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goPlayQuiz(View view){ // Método Jugar Quiz Personalizado
        if (!db.isEmpty()) { // Si la base de datos no está vacía, cambio de actividad y envío de paquete extra
            Intent i = new Intent(this, QuizActivity.class);
            i.putExtra("number", 0);
            startActivity(i);
        } else { // Si la base de datos está vacia, avisa de ello
            Toast.makeText(NewListActivity.this, getString(R.string.toast_lista_vacia), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveQuestion() { // Método para guardar una pregunta en la base de datos
        if (!vacio(etext_question) && !vacio(etext_answer1) && !vacio(etext_answer2) && !vacio(etext_answer3) && !vacio(etext_correct_answer)) {
            String pregunta = texto(etext_question);
            String respuestas = "";
            int number = new Random().nextInt(4);
            if(number == 0) { // Randomizar el orden
                respuestas = respuestas + "*" + texto(etext_correct_answer);
                respuestas = respuestas + texto(etext_answer1);
                respuestas = respuestas + texto(etext_answer2);
                respuestas = respuestas + texto(etext_answer3);
                db.insertPregunta(pregunta, respuestas);
            }else if(number == 1){
                respuestas = respuestas + texto(etext_answer1);
                respuestas = respuestas + "*" + texto(etext_correct_answer);
                respuestas = respuestas + texto(etext_answer2);
                respuestas = respuestas + texto(etext_answer3);
                db.insertPregunta(pregunta, respuestas);
            }else if (number == 2){
                respuestas = respuestas + texto(etext_answer1);
                respuestas = respuestas + texto(etext_answer2);
                respuestas = respuestas + "*" + texto(etext_correct_answer);
                respuestas = respuestas + texto(etext_answer3);
                db.insertPregunta(pregunta, respuestas);
            }else{
                respuestas = respuestas + texto(etext_answer1);
                respuestas = respuestas + texto(etext_answer2);
                respuestas = respuestas + texto(etext_answer3);
                respuestas = respuestas + "*" + texto(etext_correct_answer);
                db.insertPregunta(pregunta, respuestas);
            }
            Toast.makeText(NewListActivity.this, R.string.toast_insertada, Toast.LENGTH_SHORT).show();
        } else { // Si algún campo está vacío, impide insertarla
            Toast.makeText(NewListActivity.this, R.string.toast_campos_vacíos, Toast.LENGTH_SHORT).show();
        }
    }

    public String texto(EditText editText) { // Método para parsear
        String texto;
        texto = editText.getText().toString() + ";";
        return texto;
    }

    public boolean vacio(EditText editText) { // Método para comprobar campos vacíos
        if (editText.getText().toString().matches("")) {
            return true;
        } else {
            return false;
        }
    }
}



