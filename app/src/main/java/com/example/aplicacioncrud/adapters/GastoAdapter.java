package com.example.aplicacioncrud.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacioncrud.R;
import com.example.aplicacioncrud.models.Gasto;

import java.util.List;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.ViewHolder> {
    private List<Gasto> gastos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Gasto gasto);
    }

    public GastoAdapter(List<Gasto> gastos, OnItemClickListener listener) {
        this.gastos = gastos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gasto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gasto gasto = gastos.get(position);
        holder.lblMonto.setText("$ " + gasto.monto);
        holder.lblCategoria.setText(gasto.categoria);

        holder.lblDescripcion.setText(gasto.descripcion);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(gasto));
    }

    @Override
    public int getItemCount() {
        return gastos != null ? gastos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblMonto, lblCategoria, lblDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblMonto = itemView.findViewById(R.id.lblMonto);
            lblCategoria = itemView.findViewById(R.id.lblCategoria);
            lblDescripcion = itemView.findViewById(R.id.lblDescripcion);
        }
    }
}