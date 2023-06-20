package com.example.suculentphoto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suculentphoto.retrofit.APIClient;
import com.example.suculentphoto.retrofit.APIRESTSuculentPhoto;
import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;
import com.example.suculentphoto.retrofit.pojo.SintomaBasico;
import com.example.suculentphoto.util.DialogosUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<SintomaBasico> sintomasList;
    private Spinner spinnerSintomas;
    private ImageButton botonAgregarSintoma;
    private TextInputEditText textSintoma;
    private TextInputEditText textDescripcionSintoma;
    private Button btnCancelarSintoma;
    private Button btnRegistrarSintoma;

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

        botonAgregarSintoma = findViewById(R.id.botonAgregarSintoma);
        spinnerSintomas = findViewById(R.id.listaSintomas);
        textSintoma = vistaModalSintomaNuevo.findViewById(R.id.textSintoma);
        textDescripcionSintoma = vistaModalSintomaNuevo.findViewById(R.id.textDescripcionSintoma);
        btnCancelarSintoma = vistaModalSintomaNuevo.findViewById(R.id.btnCancelarSintoma);
        btnRegistrarSintoma = vistaModalSintomaNuevo.findViewById(R.id.btnRegistrarSintoma);

        botonAgregarSintoma.setOnClickListener(v -> mostrarModalNuevoSintoma());
        btnCancelarSintoma.setOnClickListener(v -> cerrarModalNuevoSintoma());
        btnRegistrarSintoma.setOnClickListener(v -> registrarNuevoSintoma());

        //implementacion del servicio de consulta API
        apirestSuculentPhoto = APIClient.getClient().create(APIRESTSuculentPhoto.class);
    }

    private void consultarSintomas() {

        apirestSuculentPhoto.getListSintomas().enqueue(new Callback<RespuestaAPI<List<SintomaBasico>>>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaAPI<List<SintomaBasico>>> call, @NonNull Response<RespuestaAPI<List<SintomaBasico>>> response) {

                if (!response.isSuccessful()) {

                    try {

                        RespuestaAPI respuestaAPI = RespuestaAPI.mapearRespuestaErrorDesde(response.errorBody());
                        DialogosUtil.mostrarAlertaError(MainActivity.this, respuestaAPI);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }
                RespuestaAPI<List<SintomaBasico>> body = response.body();
                assert body != null;

                sintomasList = body.getData();

                cargarSintomasListASpinnerSintomas();

            }

            @Override
            public void onFailure(@NonNull Call<RespuestaAPI<List<SintomaBasico>>> call, @NonNull Throwable t) {
                DialogosUtil.mostrarAlerta(MainActivity.this, "ERROR consultar síntomas", t.getMessage());
            }
        });

    }

    private void cargarSintomasListASpinnerSintomas() {

        ArrayAdapter<SintomaBasico> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sintomasList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSintomas.setAdapter(adapter);
    }

    public void mostrarModalNuevoSintoma() {

        textSintoma.setText("");
        textDescripcionSintoma.setText("");
        textSintoma.requestFocus();
        modalSintomaNuevo.show();
    }

    public void cerrarModalNuevoSintoma() {
        this.modalSintomaNuevo.dismiss();
    }

    public void registrarNuevoSintoma() {

        SintomaBasico sintoma = new SintomaBasico();
        sintoma.setSintoma(textSintoma.getText().toString());
        sintoma.setDescripcion(textDescripcionSintoma.getText().toString());

        apirestSuculentPhoto.postRegistrarSintoma(sintoma).enqueue(new Callback<RespuestaAPI<SintomaBasico>>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaAPI<SintomaBasico>> call, @NonNull Response<RespuestaAPI<SintomaBasico>> response) {

                if (!response.isSuccessful()) {

                    try {

                        RespuestaAPI respuestaAPI = RespuestaAPI.mapearRespuestaErrorDesde(response.errorBody());
                        DialogosUtil.mostrarAlertaError(MainActivity.this, respuestaAPI);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                assert response.body() != null;

                SintomaBasico sintomaRegistrado = response.body().getData();
                sintomasList.add(sintomaRegistrado);
                cargarSintomasListASpinnerSintomas();
                spinnerSintomas.setSelection(sintomasList.size() - 1);

                cerrarModalNuevoSintoma();

                DialogosUtil.mostrarAlerta(MainActivity.this, "Registro exitoso", "Se ha registrado exitosamente!");

            }

            @Override
            public void onFailure(@NonNull Call<RespuestaAPI<SintomaBasico>> call, @NonNull Throwable t) {

                DialogosUtil.mostrarAlerta(MainActivity.this, "ERROR registro síntoma", t.getMessage());

            }
        });

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
}