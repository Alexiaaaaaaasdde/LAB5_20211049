package com.example.gestorestudioapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import com.google.android.material.appbar.MaterialToolbar; // 👈 importante
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

        // 🔹 Barra superior con botón de regreso
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        // 🔹 Campos del formulario
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

        Curso nuevo = new Curso(nombre, categoria, frecuencia, fecha + " - " + hora);

        String json = prefs.getString("listaCursos", "[]");
        Type type = new TypeToken<ArrayList<Curso>>() {}.getType();
        ArrayList<Curso> lista = gson.fromJson(json, type);

        lista.add(nuevo);
        prefs.edit().putString("listaCursos", gson.toJson(lista)).apply();

        programarRecordatorio(nombre, categoria, fecha, hora);

        Toast.makeText(this, "Curso guardado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void programarRecordatorio(String nombre, String categoria, String fecha, String hora) {
        String mensaje;
        if (categoria.toLowerCase().contains("lab")) {
            mensaje = "🧪 Completar práctica de laboratorio";
        } else {
            mensaje = "📖 Revisar apuntes de " + nombre;
        }

        Intent intent = new Intent(this, RecordatorioReceiver.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("mensaje", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar c = Calendar.getInstance();
        try {
            String[] partesFecha = fecha.split("/");
            String[] partesHora = hora.split(":");
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(partesFecha[0]));
            c.set(Calendar.MONTH, Integer.parseInt(partesFecha[1]) - 1);
            c.set(Calendar.YEAR, Integer.parseInt(partesFecha[2]));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partesHora[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(partesHora[1]));
            c.set(Calendar.SECOND, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en formato de fecha/hora", Toast.LENGTH_SHORT).show();
            return;
        }

        long tiempo = c.getTimeInMillis();
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            // Verificar permiso exact alarm (Android 12+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent intentPermiso =
                            new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intentPermiso);
                    Toast.makeText(this, "Debes permitir alarmas exactas", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Alarma exacta inicial
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, tiempo, pendingIntent);

            // Repetir según frecuencia (en horas o días)
            try {
                long intervalo;
                if (etFrecuencia.getText().toString().toLowerCase().contains("hora")) {
                    int horas = Integer.parseInt(etFrecuencia.getText().toString().replaceAll("\\D+", ""));
                    intervalo = horas * 60L * 60L * 1000L;
                } else {
                    int dias = Integer.parseInt(etFrecuencia.getText().toString().replaceAll("\\D+", ""));
                    intervalo = dias * 24L * 60L * 60L * 1000L;
                }

                alarmManager.setRepeating(
                        android.app.AlarmManager.RTC_WAKEUP,
                        tiempo + intervalo,
                        intervalo,
                        pendingIntent
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "🔔 Recordatorio programado", Toast.LENGTH_SHORT).show();
        }
    }
}
