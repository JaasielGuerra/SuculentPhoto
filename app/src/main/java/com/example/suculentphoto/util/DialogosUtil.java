package com.example.suculentphoto.util;

import android.app.AlertDialog;
import android.content.Context;

import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;

public class DialogosUtil {

    public static void mostrarAlertaError(Context context, RespuestaAPI respuestaError){

        StringBuilder errores = new StringBuilder();
        for (String error : respuestaError.getErrors()){
            errores
                    .append("\n\t")
                    .append("- ")
                    .append(error);
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(respuestaError.getMessage());
        alertDialog.setMessage(errores);
        alertDialog.setButton("OK", (dialog, which) -> alertDialog.cancel());
        alertDialog.show();
    }

    public static void mostrarAlerta(Context context, String titulo, String mensaje){

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton("OK", (dialog, which) -> alertDialog.cancel());
        alertDialog.show();
    }
}
