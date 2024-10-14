package com.example.salvadortech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Layout que contém header e footer

        // Define o título da Activity
        setTitle("Configurações"); // Título da página
    }
}
