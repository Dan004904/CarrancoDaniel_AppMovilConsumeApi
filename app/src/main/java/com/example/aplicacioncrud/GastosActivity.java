package com.example.aplicacioncrud;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GastosActivity extends AppCompatActivity {

    private int idUsuarioMaestro;
    private String nombreUsuarioMaestro;
    private RecyclerView rvGastos;
    private GastoAdapter adapter;
    private Button btnAgregarGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gastos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idUsuarioMaestro = getIntent().getIntExtra("USUARIO_ID", 0);
        nombreUsuarioMaestro = getIntent().getStringExtra("USUARIO_NOMBRE");

        if (idUsuarioMaestro == 0) {
            Toast.makeText(this, "Error de ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("Gastos: " + nombreUsuarioMaestro);

        rvGastos = findViewById(R.id.rvGastos);
        rvGastos.setLayoutManager(new LinearLayoutManager(this));
        btnAgregarGasto = findViewById(R.id.btnAgregarGasto);

        btnAgregarGasto.setOnClickListener(v -> mostrarDialogoGasto(null));

        cargarGastosDesdeAPI();
    }

    private void cargarGastosDesdeAPI() {
        new Thread(() -> {
            try {
                List<Gasto> listaGastos = ApiClient.getGastosPorUsuario(idUsuarioMaestro);

                runOnUiThread(() -> {
                    adapter = new GastoAdapter(listaGastos, this::mostrarDialogoGasto);
                    rvGastos.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(GastosActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void mostrarDialogoGasto(Gasto gastoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_gasto, null);

        EditText inputMonto = viewInflated.findViewById(R.id.txtMontoGasto);
        EditText inputCategoria = viewInflated.findViewById(R.id.txtCategoriaGasto);
        EditText inputDescripcion = viewInflated.findViewById(R.id.txtDescripcionGasto);

        if (gastoExistente != null) {
            builder.setTitle("Editar Gasto");
            inputMonto.setText(String.valueOf(gastoExistente.monto));
            inputCategoria.setText(gastoExistente.categoria);
            inputDescripcion.setText(gastoExistente.descripcion);
        } else {
            builder.setTitle("Nuevo Gasto");
        }

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String montoStr = inputMonto.getText().toString();
            String categoria = inputCategoria.getText().toString();
            String descripcion = inputDescripcion.getText().toString();

            if (!montoStr.isEmpty() && !categoria.isEmpty() && !descripcion.isEmpty()) {
                double monto = Double.parseDouble(montoStr);
                if (gastoExistente == null) {
                    crearGasto(monto, categoria, descripcion);
                } else {
                    actualizarGasto(gastoExistente.id, monto, categoria, descripcion);
                }
            } else {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        if (gastoExistente != null) {
            builder.setNeutralButton("Borrar", (dialog, which) -> borrarGasto(gastoExistente.id));
        }

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void crearGasto(double monto, String categoria, String descripcion) {
        new Thread(() -> {
            try {
                Gasto nuevo = new Gasto();
                nuevo.monto = monto;
                nuevo.categoria = categoria;
                nuevo.descripcion = descripcion;
                nuevo.usuario_id = idUsuarioMaestro;

                Gasto creado = ApiClient.createGasto(nuevo);

                runOnUiThread(() -> {
                    if (creado != null && creado.id != 0) {
                        cargarGastosDesdeAPI();
                    } else {
                        Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void actualizarGasto(int idGasto, double monto, String categoria, String descripcion) {
        new Thread(() -> {
            try {
                Gasto actualizado = new Gasto();
                actualizado.id = idGasto;
                actualizado.monto = monto;
                actualizado.categoria = categoria;
                actualizado.descripcion = descripcion;
                actualizado.usuario_id = idUsuarioMaestro;

                ApiClient.updateGasto(idGasto, actualizado);
                runOnUiThread(this::cargarGastosDesdeAPI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void borrarGasto(int idGasto) {
        new Thread(() -> {
            try {
                ApiClient.deleteGasto(idGasto);
                runOnUiThread(this::cargarGastosDesdeAPI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}