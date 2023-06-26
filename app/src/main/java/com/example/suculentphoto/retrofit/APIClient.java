package com.example.suculentphoto.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    /**
     * Conexion al dominio donde se encuntra la API
     */
    //private static String API_URL = "http://192.168.1.67:8080";
    private static String API_URL = "http://ec2-3-134-12-80.us-east-2.compute.amazonaws.com:8080";

    /**
     * Atributo static para definiri el time out por defecto del HttpClient al conectar al servidor.
     */
    private static final int TIMEOUT_CONNECT = 60;

    /**
     * Atributo static para definir el time out por defcto del HttpClient al leer datos del servidor.
     */
    private static final int TIMEOUT_READ = 60;

    /**
     * Atributo static para definir el time out por defecto del HttpClient al escribir datos en el servidor.
     */
    private static final int TIMEOUT_WRITE = 60;

    private APIClient() {
    }

    ;

    private static Gson gson_basic = new GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .build();

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson_basic))
                    .build();
        }

        return retrofit;
    }


}
