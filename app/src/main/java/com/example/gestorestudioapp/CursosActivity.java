package com.example.gestorestudioapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

        prefs = getSharedPreferences("CursosPrefs", MODE_PRIVATE);
        cargarCursos();

        adapter = new CursosAdapter(listaCursos);
        recyclerCursos.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoCursoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCursos();
    }

    // Cargar lista de cursos guardados
    private void cargarCursos() {
        String json = prefs.getString("listaCursos", "[]");
        Type type = new TypeToken<ArrayList<Curso>>() {}.getType();
        List<Curso> lista = new Gson().fromJson(json, type);

        listaCursos.clear();
        listaCursos.addAll(lista);

        if (listaCursos.isEmpty()) {
            Toast.makeText(this, "No hay cursos registrados aún", Toast.LENGTH_SHORT).show();
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
