package com.example.suculentphoto.retrofit;

import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;
import com.example.suculentphoto.retrofit.pojo.SintomaBasico;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIRESTSuculentPhoto {
    @GET("/sintomas")
    Call<RespuestaAPI<List<SintomaBasico>>> getListSintomas();

    @POST("/sintoma")
    Call<RespuestaAPI<SintomaBasico>> postRegistrarSintoma(@Body SintomaBasico sintoma);

    @Multipart
    @POST("/registrar-suculenta")
    Call<RespuestaAPI> postRegistrarSuculenta(
            @Part("idSintoma") RequestBody idSintoma,
            @Part("consejo") RequestBody consejo,
            @Part List<MultipartBody.Part> fotos
    );
}
