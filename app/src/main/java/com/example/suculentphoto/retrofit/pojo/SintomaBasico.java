package com.example.suculentphoto.retrofit.pojo;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SintomaBasico {

    @SerializedName("idSintoma")
    @Expose
    private String idSintoma;

    @SerializedName("sintoma")
    @Expose
    private String sintoma;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    public String getIdSintoma() {
        return idSintoma;
    }

    public void setIdSintoma(String idSintoma) {
        this.idSintoma = idSintoma;
    }

    public String getSintoma() {
        return sintoma;
    }

    public void setSintoma(String sintoma) {
        this.sintoma = sintoma;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return sintoma;
    }
}
