package com.example.suculentphoto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suculentphoto.retrofit.APIClient;
import com.example.suculentphoto.retrofit.APIRESTSuculentPhoto;
import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;
import com.example.suculentphoto.retrofit.pojo.SintomaBasico;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerSintomas;

    private AlertDialog modalFoto;
    private AlertDialog modalSintomaNuevo;

    APIRESTSuculentPhoto apirestSuculentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        consultarSintomas();
    }

    private void init() {

        View vistaModalFoto = getLayoutInflater().inflate(R.layout.dlg_tomar_foto, null);
        modalFoto = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Tomar foto")
                .setView(vistaModalFoto)
                .create();

        View vistaModalSintomaNuevo = getLayoutInflater().inflate(R.layout.dlg_sintoma, null);
        modalSintomaNuevo = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Registrar síntoma")
                .setView(vistaModalSintomaNuevo)
                .create();

        spinnerSintomas = findViewById(R.id.listaSintomas);

        //implementacion del servicio de consulta API
        apirestSuculentPhoto = APIClient.getClient().create(APIRESTSuculentPhoto.class);
    }

    private void consultarSintomas() {

        apirestSuculentPhoto.getListSintomas().enqueue(new Callback<RespuestaAPI<List<SintomaBasico>>>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaAPI<List<SintomaBasico>>> call, @NonNull Response<RespuestaAPI<List<SintomaBasico>>> response) {

                RespuestaAPI<List<SintomaBasico>> body = response.body();
                assert body != null;
                List<SintomaBasico> sintomasBasicosList = body.getData();

                Log.println(Log.INFO, "Sintomas", "Se ha consultado sintomas al servicio REST...");
                cargarListaASpinnerSintomas(sintomasBasicosList);

            }

            @Override
            public void onFailure(@NonNull Call<RespuestaAPI<List<SintomaBasico>>> call, @NonNull Throwable t) {
                Log.println(Log.ERROR, "Error consulta sintomas", t.getMessage());
            }
        });

    }

    private void cargarListaASpinnerSintomas(List<SintomaBasico> sintomasBasicosList ){

        ArrayAdapter<SintomaBasico> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sintomasBasicosList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSintomas.setAdapter(adapter);
    }


    public void tomarFoto1(View view) {
        this.modalFoto.show();
    }

    public void cerrarModalTomaFoto(View view) {
        this.modalFoto.dismiss();
    }

    public void aceptarTomaFotoModal(View view) {
        Toast.makeText(MainActivity.this, "¡Gracias! Tu apoyo, es de mucho valor para mi :)", Toast.LENGTH_LONG)
                .show();
    }

    public void mostrarModalNuevoSintoma(View vien) {
        this.modalSintomaNuevo.show();
    }

    public void cerrarModalNuevoSintoma(View view) {
        this.modalSintomaNuevo.dismiss();
    }

}