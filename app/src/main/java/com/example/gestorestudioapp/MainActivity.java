package com.example.gestorestudioapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE = 1;
    private TextView tvSaludo, tvMensaje;
    private ImageView imgUsuario;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvMensaje = findViewById(R.id.tvMensaje);
        imgUsuario = findViewById(R.id.imgUsuario);
        Button btnConfig = findViewById(R.id.btnConfig);
        Button btnVerCursos = findViewById(R.id.btnVerCursos);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // 🔹 Cargar nombre y mensaje guardados
        updateTexts();

        // 🔹 Cargar imagen si existe
        Bitmap savedImage = loadImage();
        if (savedImage != null) imgUsuario.setImageBitmap(savedImage);

        // 🔹 Cambiar imagen al tocarla
        imgUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // 🔹 Botones
        btnConfig.setOnClickListener(v -> startActivity(new Intent(this, ConfigActivity.class)));
        btnVerCursos.setOnClickListener(v ->
                startActivity(new Intent(this, CursosActivity.class))
        );

    }

    // 🔹 Cada vez que vuelva a esta Activity (después de guardar config)
    @Override
    protected void onResume() {
        super.onResume();
        updateTexts(); // refresca saludo y mensaje
    }

    private void updateTexts() {
        String nombre = prefs.getString("nombre", "Usuario");
        String mensaje = prefs.getString("mensaje", "Hoy es un gran día para aprender 🌞");
        tvSaludo.setText("¡Hola, " + nombre + "!");
        tvMensaje.setText(mensaje);
    }

    // 🔹 Guardar imagen seleccionada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgUsuario.setImageBitmap(bitmap);
                saveImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap bitmap) throws IOException {
        FileOutputStream fos = openFileOutput("user_image.png", MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }

    private Bitmap loadImage() {
        try {
            File file = new File(getFilesDir(), "user_image.png");
            if (file.exists()) {
                return MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
