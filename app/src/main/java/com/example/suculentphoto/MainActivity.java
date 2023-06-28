package com.example.suculentphoto;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.suculentphoto.retrofit.APIClient;
import com.example.suculentphoto.retrofit.APIRESTSuculentPhoto;
import com.example.suculentphoto.retrofit.pojo.RespuestaAPI;
import com.example.suculentphoto.retrofit.pojo.SintomaBasico;
import com.example.suculentphoto.util.DialogosUtil;
import com.example.suculentphoto.util.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String ESTADO_SALUDABLE = "SALUDABLE";
    private static final String ESTADO_ENFERMA = "ENFERMA";

    private final int CAPTURA_IMAGEN = 1;
    private int idBotonActualFoto;
    private Bitmap fotoOriginalActualTomada;
    private Bitmap miniaturaActualTomada;
    private Map<Integer, Bitmap> fotosTomadas;
    private String rutaFotoTemporalEnDisco;

    private ImageButton btnfoto1;
    private ImageButton btnfoto2;
    private ImageButton btnfoto3;
    private ImageButton btnfoto4;
    private ImageButton btnfoto5;
    private ImageButton btnfoto6;

    private ImageView previsualizacionImgSuculenta;
    private Button btnCancelarFoto;
    private Button btnAceptarFoto;

    private List<SintomaBasico> sintomasList;
    private ImageButton botonAgregarSintoma;
    private TextInputEditText textSintoma;
    private TextInputEditText textDescripcionSintoma;
    private Button btnCancelarSintoma;
    private Button btnRegistrarSintoma;

    private RadioGroup estadoPlanta;
    private RadioButton radioBtnEnferma;
    private Spinner spinnerSintomas;
    private TextInputEditText textoConsejo;

    private Button btnGuardarFotos;

    private AlertDialog modalFoto;
    private AlertDialog modalSintomaNuevo;

    APIRESTSuculentPhoto apirestSuculentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        consultarSintomas();
        resetForm();
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

        btnfoto1 = findViewById(R.id.btnfoto1);
        btnfoto2 = findViewById(R.id.btnfoto2);
        btnfoto3 = findViewById(R.id.btnfoto3);
        btnfoto4 = findViewById(R.id.btnfoto4);
        btnfoto5 = findViewById(R.id.btnfoto5);
        btnfoto6 = findViewById(R.id.btnfoto6);

        btnfoto1.setOnClickListener(this::mostrarModalTomarFoto);
        btnfoto2.setOnClickListener(this::mostrarModalTomarFoto);
        btnfoto3.setOnClickListener(this::mostrarModalTomarFoto);
        btnfoto4.setOnClickListener(this::mostrarModalTomarFoto);
        btnfoto5.setOnClickListener(this::mostrarModalTomarFoto);
        btnfoto6.setOnClickListener(this::mostrarModalTomarFoto);

        previsualizacionImgSuculenta = vistaModalFoto.findViewById(R.id.previsualizacionImgSuculenta);
        btnCancelarFoto = vistaModalFoto.findViewById(R.id.btnCancelarFoto);
        btnAceptarFoto = vistaModalFoto.findViewById(R.id.btnAceptarFoto);

        previsualizacionImgSuculenta.setOnClickListener(v -> levantarCamara());
        btnAceptarFoto.setOnClickListener(v -> aceptarTomaFotoModal());
        btnCancelarFoto.setOnClickListener(v -> cerrarModalTomarFoto());

        botonAgregarSintoma = findViewById(R.id.botonAgregarSintoma);
        spinnerSintomas = findViewById(R.id.listaSintomas);
        textSintoma = vistaModalSintomaNuevo.findViewById(R.id.textSintoma);
        textDescripcionSintoma = vistaModalSintomaNuevo.findViewById(R.id.textDescripcionSintoma);
        btnCancelarSintoma = vistaModalSintomaNuevo.findViewById(R.id.btnCancelarSintoma);
        btnRegistrarSintoma = vistaModalSintomaNuevo.findViewById(R.id.btnRegistrarSintoma);

        botonAgregarSintoma.setOnClickListener(v -> mostrarModalNuevoSintoma());
        btnCancelarSintoma.setOnClickListener(v -> cerrarModalNuevoSintoma());
        btnRegistrarSintoma.setOnClickListener(v -> registrarNuevoSintoma());

        estadoPlanta = findViewById(R.id.estadoPlanta);
        radioBtnEnferma = findViewById(R.id.radioBtnEnferma);
        textoConsejo = findViewById(R.id.textoConsejo);

        estadoPlanta.setOnCheckedChangeListener((group, checkedId) -> alternarEstadoSalud(checkedId));

        btnGuardarFotos = findViewById(R.id.btnGuardarFotos);

        btnGuardarFotos.setOnClickListener(v -> registrarFotosSuculenta());

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

    private void resetForm() {
        textoConsejo.setText("");
        radioBtnEnferma.setChecked(true);

        if (sintomasList != null && sintomasList.size() > 0) {
            spinnerSintomas.setSelection(0);
        }

        //mapa donde se guardan las fotos tomadas
        fotosTomadas = new HashMap<>();

        fotoOriginalActualTomada = null;
        miniaturaActualTomada = null;
        rutaFotoTemporalEnDisco = null;

        //resetear los botones de las fotos
        btnfoto1.setImageResource(R.drawable.foto_no_tomada);
        btnfoto2.setImageResource(R.drawable.foto_no_tomada);
        btnfoto3.setImageResource(R.drawable.foto_no_tomada);
        btnfoto4.setImageResource(R.drawable.foto_no_tomada);
        btnfoto5.setImageResource(R.drawable.foto_no_tomada);
        btnfoto6.setImageResource(R.drawable.foto_no_tomada);
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

    public void alternarEstadoSalud(int checkId) {

        String valorSeleccionado = obtenerSeleccionEstadoSalud(checkId);

        if (valorSeleccionado.equals(ESTADO_SALUDABLE)) {
            botonAgregarSintoma.setVisibility(View.GONE);
            spinnerSintomas.setVisibility(View.GONE);
            textoConsejo.setVisibility(View.GONE);
        }

        if (valorSeleccionado.equals(ESTADO_ENFERMA)) {
            botonAgregarSintoma.setVisibility(View.VISIBLE);
            spinnerSintomas.setVisibility(View.VISIBLE);
            textoConsejo.setVisibility(View.VISIBLE);
        }
    }

    private String obtenerSeleccionEstadoSalud(int checkId) {
        RadioButton radioButtonSeleccionado = findViewById(checkId);
        String valorSeleccionado = radioButtonSeleccionado.getText().toString();
        return valorSeleccionado;
    }

    private void mostrarModalTomarFoto(View view) {

        ToastUtil.showToastCorto(this, "Tomar " + view.getContentDescription());

        //salvar el id del boton para saber que foto se esta tomando
        idBotonActualFoto = view.getId();

        //poner el icono de tomar foto
        previsualizacionImgSuculenta.setImageResource(obtenerRecursoFotoSegunBotonActual());

        //si ya hay una foto, ponerla en la previsualizacion
        Bitmap imagenTomada = fotosTomadas.get(idBotonActualFoto);
        if (imagenTomada != null) {
            previsualizacionImgSuculenta.setImageBitmap(imagenTomada);
        }

        modalFoto.setTitle("Tomar " + view.getContentDescription());
        modalFoto.show();
    }

    private int obtenerRecursoFotoSegunBotonActual() {

        if (idBotonActualFoto == R.id.btnfoto1) {
            return R.drawable.foto_1;
        } else if (idBotonActualFoto == R.id.btnfoto2) {
            return R.drawable.foto_2;
        } else if (idBotonActualFoto == R.id.btnfoto3) {
            return R.drawable.foto_3;
        } else if (idBotonActualFoto == R.id.btnfoto4) {
            return R.drawable.foto_4;
        } else if (idBotonActualFoto == R.id.btnfoto5) {
            return R.drawable.foto_5;
        } else if (idBotonActualFoto == R.id.btnfoto6) {
            return R.drawable.foto_6;
        }

        throw new RuntimeException("Ningúna opción hizo match al escoger el recurso según botón presionado");
    }

    private void levantarCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            File archivoTemporal = null;

            try {

                archivoTemporal = crearArchivoTemporal();

            } catch (IOException ex) {
                DialogosUtil.mostrarAlerta(this, "ERROR", ex.getMessage());
                return;
            }

            Uri fotoUri = FileProvider.getUriForFile(this, "com.example.suculentphoto.fileprovider", archivoTemporal);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, CAPTURA_IMAGEN);
        }
    }

    private File crearArchivoTemporal() throws IOException {

        //String nombreArchivo = "captura_temp_";
        String nombreArchivo = "captura_temp.jpg";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crea el archivo temporal en el directorio de imágenes de la aplicación
        //File imagenTemporal = File.createTempFile(nombreArchivo, ".jpg", directorio);
        File imagenTemporal = new File(directorio, nombreArchivo);
        imagenTemporal.createNewFile();

        rutaFotoTemporalEnDisco = imagenTemporal.getAbsolutePath();

        return imagenTemporal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURA_IMAGEN && resultCode == RESULT_OK) {

            Bitmap bitmapOriginal = obtenerFotoOriginalSuculenta();
            fotoOriginalActualTomada = rotateImageIfRequired(bitmapOriginal, rutaFotoTemporalEnDisco);
            miniaturaActualTomada = obtenerMiniaturaSuculenta(fotoOriginalActualTomada);

            mostrarFotoOriginalActualEnPrevisualizacionModal();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int rotationAngle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
            default:
                return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private Bitmap obtenerFotoOriginalSuculenta() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(rutaFotoTemporalEnDisco, options);

        int anchoOriginal = options.outWidth;
        int altoOriginal = options.outHeight;

        int anchoDeseado = 800; // Ancho deseado para la imagen reducida


        float proporcion = (float) anchoOriginal / anchoDeseado;
        int altoDeseado = Math.round(altoOriginal / proporcion);

        // Decodificar el archivo en un Bitmap con las dimensiones reducidas
        options.inJustDecodeBounds = false;
        options.inSampleSize = calcularFactorEscala(anchoOriginal, altoOriginal, anchoDeseado, altoDeseado);

        return BitmapFactory.decodeFile(rutaFotoTemporalEnDisco, options);
    }

    // Función para calcular el factor de escala adecuado para la reducción
    private int calcularFactorEscala(int anchoOriginal, int altoOriginal, int anchoDeseado, int altoDeseado) {
        int factorEscala = 1;
        if (altoOriginal > altoDeseado || anchoOriginal > anchoDeseado) {
            final int mitadAncho = anchoOriginal / 2;
            final int mitadAlto = altoOriginal / 2;

            while ((mitadAlto / factorEscala) >= altoDeseado && (mitadAncho / factorEscala) >= anchoDeseado) {
                factorEscala *= 2;
            }
        }
        return factorEscala;
    }

    private Bitmap obtenerMiniaturaSuculenta(Bitmap bitmapOriginal) {
        return Bitmap.createScaledBitmap(bitmapOriginal, 150, 150, false);
    }

    private void mostrarFotoOriginalActualEnPrevisualizacionModal() {
        previsualizacionImgSuculenta.setImageBitmap(fotoOriginalActualTomada);
    }

    private void aceptarTomaFotoModal() {

        //si no hay foto, pedir que la tome
        if (fotoOriginalActualTomada == null) {
            DialogosUtil.mostrarAlerta(this, "Tomar foto", "Aún no ha tomado una foto.");
            return;
        }

        mostrarMiniaturaFotoTomadaEnBotonFotoActual();

        //guardar la foto original en el mapa
        fotosTomadas.put(idBotonActualFoto, fotoOriginalActualTomada);

        //reiniciar
        fotoOriginalActualTomada = null;

        modalFoto.dismiss();
    }

    private void mostrarMiniaturaFotoTomadaEnBotonFotoActual() {
        ImageButton btnFotoActual = findViewById(idBotonActualFoto);
        btnFotoActual.setImageBitmap(miniaturaActualTomada);
    }

    private void cerrarModalTomarFoto() {
        modalFoto.dismiss();
    }

    private void registrarFotosSuculenta() {

        if (fotosTomadas.isEmpty()) {
            DialogosUtil.mostrarAlerta(MainActivity.this, "SIN FOTOS", "Parece que aún no ha tomado ningúna foto.");
            return;
        }

        List<MultipartBody.Part> listaFotosParts = crearListaFotosComoMultipartBodyPart();
        RequestBody requestBodyIdSintoma = crearRequestBodyTextPlainIdSintoma();
        RequestBody requestBodyConsejo = crearRequestBodyTextPlainConsejo();

        AlertDialog alertDialogCargando = DialogosUtil.mostrarAlertaCargando(this);

        apirestSuculentPhoto.postRegistrarSuculenta(
                requestBodyIdSintoma,
                requestBodyConsejo,
                listaFotosParts
        ).enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaAPI> call, @NonNull Response<RespuestaAPI> response) {

                //cerrar alerta cargando
                alertDialogCargando.dismiss();

                if (!response.isSuccessful()) {

                    try {

                        RespuestaAPI respuestaAPI = RespuestaAPI.mapearRespuestaErrorDesde(response.errorBody());
                        DialogosUtil.mostrarAlertaError(MainActivity.this, respuestaAPI);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                RespuestaAPI body = response.body();
                assert body != null;

                resetForm();
                DialogosUtil.mostrarAlerta(MainActivity.this, "Registro exitoso!", body.getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaAPI> call, @NonNull Throwable t) {

                //cerrar alerta cargando
                alertDialogCargando.dismiss();

                DialogosUtil.mostrarAlerta(MainActivity.this, "ERROR registrando suculenta", t.getMessage());
            }
        });
    }


    private List<MultipartBody.Part> crearListaFotosComoMultipartBodyPart() {

        List<MultipartBody.Part> listaMultipartBodyPart = new ArrayList<>();

        List<byte[]> listaFotos = obtenerListaFotosComoByteArray();
        for (int i = 0; i < listaFotos.size(); i++) {

            byte[] item = listaFotos.get(i);
            listaMultipartBodyPart.add(bytesToMultipartBody(item, "foto_" + i + 1));
        }

        return listaMultipartBodyPart;
    }

    private List<byte[]> obtenerListaFotosComoByteArray() {

        List<byte[]> listaFotos = new ArrayList<>();

        Set<Map.Entry<Integer, Bitmap>> entries = fotosTomadas.entrySet();
        for (Map.Entry<Integer, Bitmap> item : entries) {
            byte[] bytes = bitmapToByteArray(item.getValue());
            listaFotos.add(bytes);
        }

        return listaFotos;
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private MultipartBody.Part bytesToMultipartBody(byte[] bytes, String fileName) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bytes);
        return MultipartBody.Part.createFormData("fotos", fileName, requestBody);
    }

    private RequestBody crearRequestBodyTextPlainIdSintoma() {

        String estadoSalud = obtenerSeleccionEstadoSalud(estadoPlanta.getCheckedRadioButtonId());

        // si se selecciona SALUDABLE, entonces se manda el id precargado para sintoma saludable
        if (estadoSalud.equals(ESTADO_SALUDABLE)) {
            return RequestBody.create(MediaType.parse("text/plain"), ESTADO_SALUDABLE);
        }

        SintomaBasico sintomaBasico = sintomasList.get(spinnerSintomas.getSelectedItemPosition());
        return RequestBody.create(MediaType.parse("text/plain"), sintomaBasico.getIdSintoma());
    }

    private RequestBody crearRequestBodyTextPlainConsejo() {
        String consejo = textoConsejo.getText().toString();
        return RequestBody.create(MediaType.parse("text/plain"), consejo);
    }
}