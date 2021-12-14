package com.grupo14.quiziparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private NewListActivity newListActivity = new NewListActivity();
    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String ANSWER = "answer";
    private  int id_answers[] = {
            R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4
    };
    private String[] all_questions; // Variables del test
    private int correct_answer;
    private int current_question;
    private int[] answer;
    private boolean[] answer_is_correct; // Resultados
    private TextView text_question; // Layout
    private RadioGroup group;
    private Button btn_next, btn_prev;
    private int number2 = 1; // Extras
    private String dato[];
    private DataBaseSQL db = new DataBaseSQL(this); // Instanciación de la BD

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CORRECT_ANSWER, correct_answer);
        outState.putInt(CURRENT_QUESTION, current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT, answer_is_correct);
        outState.putIntArray(ANSWER, answer);
    }

    @Override
    protected void onStop() {
        Log.i("lifecycle", "onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.i("lifecycle", "onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.i("lifecycle", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // findViews
        text_question = (TextView) findViewById(R.id.text_question);
        group = (RadioGroup) findViewById(R.id.answer_group);
        btn_next = (Button) findViewById(R.id.btn_check);
        btn_prev = (Button) findViewById(R.id.btn_prev);

        // Extras
        dato = getIntent().getStringArrayExtra("data");
        number2 = getIntent().getIntExtra("number",1);

        if(number2 == 0) { // Si el quiz es customizado
            String[] lista = db.getLista().split("\n");
            all_questions = lista;
        }else { // Si es el quiz predeterminado
            all_questions = getResources().getStringArray(R.array.all_questions);
        }
        startOver();

        if(savedInstanceState == null){ // Si no hay instancias previas, vuelve a empezar
            startOver();
        }else { // Si las hay, las reanuda
            correct_answer = savedInstanceState.getInt(CORRECT_ANSWER);
            current_question = savedInstanceState.getInt(CURRENT_QUESTION);
            answer_is_correct = savedInstanceState.getBooleanArray(ANSWER_IS_CORRECT);
            answer = savedInstanceState.getIntArray(ANSWER);
            showQuestion();
        }

        btn_next.setOnClickListener(new View.OnClickListener() { // Botón siguiente
            @Override
            public void onClick(View v) {
                checkAnswer();
                if(current_question < all_questions.length-1) { // Si hay siguiente pregunta
                    current_question++; // Pasa a la siguiente pregunta
                    showQuestion();
                }else { // Si es la última
                    checkResult(); // Enseña los resultados
                }
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() { // Botón anterior
            @Override
            public void onClick(View v) {
                checkAnswer();
                if (current_question > 0){ // Si no es la primera
                    current_question--; // Vuelve a la anterior
                    showQuestion();
                }
            }
        });
    }

    private void startOver() { // Método para empezar desde el principio
        answer_is_correct = new boolean[all_questions.length]; // Reinicio de arrays y variables
        answer = new int[all_questions.length];
        for (int i = 0; i < answer.length; i++){
            answer[i] = -1;
        }
        current_question = 0;
        showQuestion();
    }

    private void checkResult() { // Método para comprobar resultados finales
        int correctas = 0, incorrectas = 0, nocontestadas = 0;
        for(int i = 0; i < all_questions.length; i++){ // Cálculo de las correctas
            if (answer_is_correct[i]) correctas++;
            else if (answer[i] == -1) nocontestadas++;
            else incorrectas++;
        }
        String message = String.format("Correctas: %d\nIncorrectas %d\nNo contestadas: %d", correctas, incorrectas, nocontestadas);

        // Diálogo para mostrar los resultados
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.result);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() { // Finalizar
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() { // Volver a empezar
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startOver();
            }
        });
        builder.create().show();
    }

    private void checkAnswer() { // Método para comprobar respuestas
        int id = group.getCheckedRadioButtonId();
        int ans = -1;
        for (int i = 0; i < id_answers.length; i++){
            if (id_answers[i] == id){
                ans = i;
            }
        }
        answer_is_correct[current_question] = (ans == correct_answer);
        answer[current_question] = ans;
    }

    private void showQuestion() { // Método para mostrar la pregunta
        String[] parts;
        String q = all_questions[current_question]; // De todas las preguntas, escoge la actual
        parts = q.split(";"); // Separa el string de las preguntas y las respuestas en el array
        group.clearCheck(); // Desmarca las respuestas
        text_question.setText(parts[0]); // Asigna el texto de la pregunta al TextView

        for (int i = 0; i < id_answers.length; i++) { // Bucle según la cantidad de respuestas
            RadioButton rb = (RadioButton) findViewById(id_answers[i]); // Cicla por cada botón
            String ans = parts[i+1]; // Cicla por cada respuesta
            if (ans.charAt(0) =='*') { // Si la respuesta es la correcta
                correct_answer =i; // Asigna la pregunta correcta
                ans = ans.substring(1); // Desecha el "*"
            }
            rb.setText(ans); // Asigna el texto de la respuesta al botón
            if (answer[current_question] == i) { // Selección del botón
                rb.setChecked(true);
            }
        }

        if (current_question == 0){ // Si es la primera pregunta, no hay botón previo
            btn_prev.setVisibility(View.GONE);
        }else { // Si no lo es, sí
            btn_prev.setVisibility(View.VISIBLE);
        }
        if (current_question == all_questions.length - 1) { // Si es la última pregunta, el botón siguiente será finalizar
            btn_next.setText(R.string.finish);
        } else { // Si no, seguirá siendo siguiente
            btn_next.setText(R.string.next);
        }
    }
}