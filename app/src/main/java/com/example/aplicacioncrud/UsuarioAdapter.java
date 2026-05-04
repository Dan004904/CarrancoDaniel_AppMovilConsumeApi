package com.example.aplicacioncrud;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> usuarios;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(Usuario usuario);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.usuarios = usuarios;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.lblNombre.setText(usuario.nombre);
        holder.lblEmail.setText(usuario.email);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(usuario));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(usuario);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return usuarios != null ? usuarios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblNombre, lblEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.lblNombre);
            lblEmail = itemView.findViewById(R.id.lblEmail);
        }
    }
}