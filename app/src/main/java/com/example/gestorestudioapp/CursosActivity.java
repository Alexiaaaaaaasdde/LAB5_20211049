package com.example.gestorestudioapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CursosActivity extends Activity {

    private RecyclerView recyclerCursos;
    private CursosAdapter adapter;
    private List<Curso> listaCursos = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        recyclerCursos = findViewById(R.id.recyclerCursos);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddCurso);

        recyclerCursos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CursosAdapter(listaCursos);
        recyclerCursos.setAdapter(adapter);

        prefs = getSharedPreferences("CursosPrefs", MODE_PRIVATE);

        // 🔹 Cargar cursos guardados si existen (por ahora, temporal)
        loadCursosEjemplo();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoCursoActivity.class);
            startActivity(intent);
        });
    }

    private void loadCursosEjemplo() {
        // Temporalmente agregamos un curso de prueba
        listaCursos.add(new Curso("Comunicaciones Ópticas", "Obligatorio", "Cada 2 días", "25/10/2025 - 09:00 AM"));
        listaCursos.add(new Curso("Laboratorio de IoT", "Laboratorio", "Cada 3 días", "26/10/2025 - 10:30 AM"));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCursos();
    }

    private void cargarCursos() {
        String json = prefs.getString("listaCursos", "[]");
        Type type = new com.google.gson.reflect.TypeToken<ArrayList<Curso>>() {}.getType();
        List<Curso> lista = new com.google.gson.Gson().fromJson(json, type);
        listaCursos.clear();
        listaCursos.addAll(lista);
        adapter.notifyDataSetChanged();
    }

}
