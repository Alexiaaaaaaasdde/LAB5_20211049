package com.example.gestorestudioapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.ViewHolder> {

    private List<Curso> listaCursos;
    private Context context;
    private Gson gson = new Gson();

    public CursosAdapter(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvNombre.setText(curso.getNombre());
        holder.tvCategoria.setText("Tipo: " + curso.getCategoria());
        holder.tvFrecuencia.setText("Frecuencia: " + curso.getFrecuencia());
        holder.tvFecha.setText("Próxima sesión: " + curso.getProximaSesion());

        // 🗑️ Botón eliminar
        holder.btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar(position));
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    // 📦 ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvFrecuencia, tvFecha;
        Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCurso);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaCurso);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuenciaCurso);
            tvFecha = itemView.findViewById(R.id.tvFechaCurso);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    // ⚠️ Diálogo de confirmación
    private void mostrarDialogoEliminar(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar curso")
                .setMessage("¿Seguro que deseas eliminar este curso?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarCurso(position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // 🧩 Eliminar curso de lista y almacenamiento local
    private void eliminarCurso(int position) {
        listaCursos.remove(position);
        notifyItemRemoved(position);

        // 🔹 Actualizar SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("CursosPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("listaCursos", gson.toJson(listaCursos)).apply();

        Toast.makeText(context, "Curso eliminado ✅", Toast.LENGTH_SHORT).show();
    }
}
