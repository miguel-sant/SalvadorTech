package com.example.salvadortech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help); // Layout que contém header e footer

        // Define o título da Activity
        setTitle("Ajuda e FAQ"); // Título da página
    }
}
