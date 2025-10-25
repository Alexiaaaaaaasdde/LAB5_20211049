package com.example.gestorestudioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.ViewHolder> {

    private List<Curso> listaCursos;

    public CursosAdapter(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvNombre.setText(curso.getNombre());
        holder.tvCategoria.setText("Tipo: " + curso.getCategoria());
        holder.tvFrecuencia.setText("Frecuencia: " + curso.getFrecuencia());
        holder.tvFecha.setText("Próxima sesión: " + curso.getProximaSesion());
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvFrecuencia, tvFecha;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCurso);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaCurso);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuenciaCurso);
            tvFecha = itemView.findViewById(R.id.tvFechaCurso);
        }
    }
}
