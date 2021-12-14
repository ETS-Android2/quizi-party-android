package com.grupo14.quiziparty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    public void goToList(View view){
        Intent i = new Intent(this, QuizActivity.class);
        startActivity(i);
    }
    public void goToNewList(View view){
        Intent i = new Intent(this, NewListActivity.class);
        startActivity(i);
    }
}