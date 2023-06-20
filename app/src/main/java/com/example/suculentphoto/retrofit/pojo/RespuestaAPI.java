package com.example.suculentphoto.retrofit.pojo;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.ResponseBody;

public class RespuestaAPI<T> {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private T data;

    @SerializedName("errors")
    @Expose
    private String[] errors;

    public static RespuestaAPI mapearRespuestaErrorDesde(ResponseBody errorBody) throws IOException {
        Gson gson = new Gson();
        RespuestaAPI respuestaAPI = gson.fromJson(errorBody.string(), RespuestaAPI.class);
        return respuestaAPI;
    }

    public static String construirMensajeErrorDesde(ResponseBody errorBody) throws IOException {

        RespuestaAPI respuestaAPI = RespuestaAPI.mapearRespuestaErrorDesde(errorBody);

        StringBuilder mensajeError = new StringBuilder();
        mensajeError.append(respuestaAPI.getMessage());

        for (String error : respuestaAPI.getErrors()){
            mensajeError
                    .append("\n\t")
                    .append(error);
        }

        return mensajeError.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String[]  getErrors() {
        return errors;
    }

    public void setErrors(String[]  errors) {
        this.errors = errors;
    }
}
