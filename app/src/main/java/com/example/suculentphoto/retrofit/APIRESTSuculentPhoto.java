package com.example.suculentphoto.retrofit;

import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;
import com.example.suculentphoto.retrofit.pojo.SintomaBasico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface APIRESTSuculentPhoto {
    @Headers({"Accept: application/json; Content-Type: application/json"})
    @GET("/sintomas")
    Call<RespuestaAPI<List<SintomaBasico>>> getListSintomas();
}
