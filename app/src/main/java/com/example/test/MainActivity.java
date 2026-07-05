package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnSemen, btnUrine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSemen = findViewById(R.id.btnSemen);
        btnUrine = findViewById(R.id.btnUrine);

        btnSemen.setOnClickListener(v ->
                openPage("semen.html"));

        btnUrine.setOnClickListener(v ->
                openPage("urine.html"));
        Button btnSaved;

        btnSaved = findViewById(R.id.btnSaved);

        btnSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedActivity.class)));
    }

    void openPage(String file){
        Intent i = new Intent(this, WebActivity.class);
        i.putExtra("file", file);
        startActivity(i);
    }
}