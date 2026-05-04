package com.example.aplicacioncrud;

import android.app.AlertDialog;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private UsuarioAdapter adapter;
    private Button btnAgregarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvUsuarios = findViewById(R.id.rvUsuarios);
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);

        btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoUsuario(null));

        cargarUsuariosDesdeAPI();
    }

    private void cargarUsuariosDesdeAPI() {
        new Thread(() -> {
            try {
                List<Usuario> listaUsuarios = ApiClient.getUsuarios();

                runOnUiThread(() -> {
                    adapter = new UsuarioAdapter(listaUsuarios, usuario -> {
                        Intent intent = new Intent(MainActivity.this, GastosActivity.class);
                        intent.putExtra("USUARIO_ID", usuario.id);
                        intent.putExtra("USUARIO_NOMBRE", usuario.nombre);
                        startActivity(intent);
                    }, this::mostrarDialogoUsuario);
                    rvUsuarios.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void mostrarDialogoUsuario(Usuario usuarioExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_usuario, null);
        EditText inputNombre = viewInflated.findViewById(R.id.txtNuevoNombre);
        EditText inputEmail = viewInflated.findViewById(R.id.txtNuevoEmail);

        if (usuarioExistente != null) {
            builder.setTitle("Editar Usuario");
            inputNombre.setText(usuarioExistente.nombre);
            inputEmail.setText(usuarioExistente.email);
        } else {
            builder.setTitle("Nuevo Usuario");
        }

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString();
            String email = inputEmail.getText().toString();

            if (!nombre.isEmpty() && !email.isEmpty()) {
                if (usuarioExistente == null) {
                    crearUsuario(nombre, email);
                } else {
                    actualizarUsuario(usuarioExistente.id, nombre, email);
                }
            } else {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        if (usuarioExistente != null) {
            builder.setNeutralButton("Borrar", (dialog, which) -> borrarUsuario(usuarioExistente.id));
        }

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void crearUsuario(String nombre, String email) {
        new Thread(() -> {
            try {
                Usuario nuevo = new Usuario();
                nuevo.nombre = nombre;
                nuevo.email = email;

                Usuario creado = ApiClient.createUsuario(nuevo);

                runOnUiThread(() -> {
                    if (creado != null && creado.id != 0) {
                        cargarUsuariosDesdeAPI();
                    } else {
                        Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void actualizarUsuario(int id, String nombre, String email) {
        new Thread(() -> {
            try {
                Usuario actualizado = new Usuario();
                actualizado.id = id;
                actualizado.nombre = nombre;
                actualizado.email = email;

                ApiClient.updateUsuario(id, actualizado);
                runOnUiThread(this::cargarUsuariosDesdeAPI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void borrarUsuario(int id) {
        new Thread(() -> {
            try {
                ApiClient.deleteUsuario(id);
                runOnUiThread(this::cargarUsuariosDesdeAPI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}