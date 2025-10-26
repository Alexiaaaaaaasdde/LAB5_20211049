package com.example.gestorestudioapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class RecordatorioReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreCurso = intent.getStringExtra("nombre");
        String mensaje = intent.getStringExtra("mensaje");

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Canal de notificación (Android 8+)
        String canalId = "CANAL_GENERAL";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Recordatorios de Cursos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Notificaciones de recordatorios por curso");
            canal.enableVibration(true);
            canal.enableLights(true);
            manager.createNotificationChannel(canal);
        }

        Intent abrirApp = new Intent(context, CursosActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                abrirApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_notificacion) // asegúrate de tener este ícono en drawable
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notificacion))
                .setContentTitle("📘 " + nombreCurso)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
