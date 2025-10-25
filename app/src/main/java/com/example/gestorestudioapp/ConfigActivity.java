package com.example.gestorestudioapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

public class ConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etMensaje = findViewById(R.id.etMensaje);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre", ""));
        etMensaje.setText(prefs.getString("mensaje", ""));

        btnGuardar.setOnClickListener(v -> {
            prefs.edit()
                    .putString("nombre", etNombre.getText().toString())
                    .putString("mensaje", etMensaje.getText().toString())
                    .apply();
            Toast.makeText(this, "Datos guardados correctamente ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
