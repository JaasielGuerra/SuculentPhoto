package com.example.suculentphoto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

    private AlertDialog modalFoto;
    private AlertDialog modalSintomaNuevo;

    APIRESTSuculentPhoto apirestSuculentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //implementacion del servicio de consulta API
        apirestSuculentPhoto = APIClient.getClient().create(APIRESTSuculentPhoto.class);
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

    public void cerrarModalNuevoSintoma(View view){
        this.modalSintomaNuevo.dismiss();
    }

    public void consultarSintomas(View view){

        apirestSuculentPhoto.getListSintomas().enqueue(new Callback<RespuestaAPI<List<SintomaBasico>>>() {
            @Override
            public void onResponse(Call<RespuestaAPI<List<SintomaBasico>>> call, Response<RespuestaAPI<List<SintomaBasico>>> response) {

                RespuestaAPI<List<SintomaBasico>> body = response.body();

                assert body != null;

                body.getData()
                        .forEach(item->{
                            System.out.println(item.getIdSintoma());
                            System.out.println(item.getSintoma());
                            System.out.println(item.getDescripcion());
                        });

            }

            @Override
            public void onFailure(Call<RespuestaAPI<List<SintomaBasico>>> call, Throwable t) {
                System.out.println("error :" + t.getMessage());
            }
        });

    }
}