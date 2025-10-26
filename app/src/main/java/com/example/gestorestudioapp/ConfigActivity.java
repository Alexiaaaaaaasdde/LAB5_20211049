package com.example.gestorestudioapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

public class ConfigActivity extends Activity {

    private EditText etNombre, etMensaje, etMotivacion, etMotivacionHoras;
    private Button btnGuardar, btnGuardarMotivacion;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        etNombre = findViewById(R.id.etNombre);
        etMensaje = findViewById(R.id.etMensaje);
        btnGuardar = findViewById(R.id.btnGuardar);

        etMotivacion = findViewById(R.id.etMotivacion);
        etMotivacionHoras = findViewById(R.id.etMotivacionHoras);
        btnGuardarMotivacion = findViewById(R.id.btnGuardarMotivacion);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        etNombre.setText(prefs.getString("nombre", ""));
        etMensaje.setText(prefs.getString("mensaje", ""));
        etMotivacion.setText(prefs.getString("motivacionMensaje", ""));
        etMotivacionHoras.setText(
                String.valueOf(prefs.getInt("motivacionHoras", 0))
        );

        btnGuardar.setOnClickListener(v -> {
            prefs.edit()
                    .putString("nombre", etNombre.getText().toString())
                    .putString("mensaje", etMensaje.getText().toString())
                    .apply();
            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });

        //Guardar configuración motivacional
        btnGuardarMotivacion.setOnClickListener(v -> {
            String mensaje = etMotivacion.getText().toString();
            String horasStr = etMotivacionHoras.getText().toString();

            if (mensaje.isEmpty() || horasStr.isEmpty()) {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int horas = Integer.parseInt(horasStr);

            prefs.edit()
                    .putString("motivacionMensaje", mensaje)
                    .putInt("motivacionHoras", horas)
                    .apply();

            programarMotivacion(mensaje, horas);
            Toast.makeText(this, "Notificación motivacional configurada", Toast.LENGTH_SHORT).show();
        });
    }

    private void programarMotivacion(String mensaje, int horas) {
        Intent intent = new Intent(this, RecordatorioReceiver.class);
        intent.putExtra("nombre", "Motivación Personal");
        intent.putExtra("mensaje", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 9999, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long tiempoInicial = System.currentTimeMillis() + horas * 60L * 60L * 1000L;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    tiempoInicial,
                    horas * 60L * 60L * 1000L,
                    pendingIntent
            );
        }
    }
}
