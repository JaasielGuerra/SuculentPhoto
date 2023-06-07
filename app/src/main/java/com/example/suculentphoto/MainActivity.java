package com.example.suculentphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AlertDialog modalFoto;
    private AlertDialog modalSintomaNuevo;

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
}