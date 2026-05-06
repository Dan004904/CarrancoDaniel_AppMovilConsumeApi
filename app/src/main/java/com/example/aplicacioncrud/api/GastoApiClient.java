package com.example.aplicacioncrud.api;

import com.example.aplicacioncrud.models.Gasto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GastoApiClient {
    private static final String BASE_URL = "https://api-gastos-movil.onrender.com/";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.parse("application/json");

    // GET /usuarios/gastos
    public static List<Gasto> getGasto(int usuarioId) throws Exception {
        Request request = new Request.Builder().url(BASE_URL + "usuarios/" + usuarioId + "/gastos")
                .build();
        Response response = client.newCall(request).execute();
        String json = response.body() != null ? response.body().string() : "[]";
        return gson.fromJson(json, new TypeToken<List<Gasto>>() {
        }.getType());
    }

    // POST /gastos
    public static Gasto createGasto(Gasto gasto) throws Exception {
        final Gasto[] result = {null};
        Thread t = new Thread(() -> {
            try {
                Request request = new Request.Builder().url(BASE_URL + "gastos")
                        .post(RequestBody.create(gson.toJson(gasto), JSON)).build();
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                String json = body != null ? body.string() : "{}";
                result[0] = gson.fromJson(json, Gasto.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result[0];
    }

    // PUT /gastos/{id}
    public static Gasto updateGasto(int id, Gasto gasto) throws Exception {
        final Gasto[] result = {null};
        Thread t = new Thread(() -> {
            try {
                Request request = new Request.Builder().url(BASE_URL + "gastos/" + id)
                        .put(RequestBody.create(gson.toJson(gasto), JSON)).build();
                Response response = client.newCall(request).execute();
                String json = response.body() != null ? response.body().string() : "{}";
                result[0] = gson.fromJson(json, Gasto.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result[0];
    }

    // DELETE /gastos/{id}
    public static void deleteGasto(int id) throws Exception {
        Thread t = new Thread(() -> {
            try {
                Request request = new Request.Builder().url(BASE_URL + "gastos/" + id)
                        .delete().build();
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
