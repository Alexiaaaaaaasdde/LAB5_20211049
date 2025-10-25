package com.example.gestorestudioapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class NuevoCursoActivity extends Activity {

    private EditText etNombre, etCategoria, etFrecuencia, etFecha, etHora;
    private Button btnGuardar;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_curso);

        etNombre = findViewById(R.id.etNombreCurso);
        etCategoria = findViewById(R.id.etCategoria);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        btnGuardar = findViewById(R.id.btnGuardarCurso);

        prefs = getSharedPreferences("CursosPrefs", MODE_PRIVATE);

        // --- Selector de fecha ---
        etFecha.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this, (view, y, m, d) -> {
                etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y));
            }, year, month, day).show();
        });

        // --- Selector de hora ---
        etHora.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(this, (view, h, m) -> {
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            }, hour, minute, true).show();
        });

        // --- Botón guardar ---
        btnGuardar.setOnClickListener(v -> guardarCurso());
    }

    private void guardarCurso() {
        String nombre = etNombre.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String frecuencia = etFrecuencia.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        if (nombre.isEmpty() || categoria.isEmpty() || frecuencia.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Crear nuevo curso
        Curso nuevo = new Curso(nombre, categoria, frecuencia, fecha + " - " + hora);

        // 🔹 Obtener lista guardada
        String json = prefs.getString("listaCursos", "[]");
        Type type = new TypeToken<ArrayList<Curso>>() {}.getType();
        ArrayList<Curso> lista = gson.fromJson(json, type);

        // 🔹 Agregar curso y guardar
        lista.add(nuevo);
        prefs.edit().putString("listaCursos", gson.toJson(lista)).apply();

        Toast.makeText(this, "Curso guardado correctamente ✅", Toast.LENGTH_SHORT).show();
        finish(); // volver a la lista
    }
}
