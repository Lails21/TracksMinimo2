package dsa.eetac.upc.edu.tracksminimo2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTrackActivity extends AppCompatActivity {
    //Declarar textview y botones que aparecen en el layout para pasar valor
    private TextView updateTrackTitle;
    private TextView updateTrackSinger;
    private Button updateTrackbtn;
    //Declarar API
    private APIRest myapirest;
    //Creamos un nuevo track
    public Track updateTrack;
    //Declaramos el spinner de cargando en el Activity donde estamos esperando los datos
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Definimos el nombre del layout que debe abrirse con esta clase
        setContentView(R.layout.update_track_layout);

        // Identificamos con el nombre que tengan los TextView/button del xml (update_track_layout)
        updateTrackTitle = findViewById(R.id.update_track_title);
        updateTrackSinger = findViewById(R.id.update_track_singer);
        updateTrackbtn = findViewById(R.id.update_track_btn);

        //Obtiene el Intent para inicia la actividad y extraigo los strings de cada campo
        Intent intent = getIntent();
        String messageId = intent.getStringExtra("TRACK ID");
        String[] messageIdParts = messageId.split(" ");
        int id = Integer.parseInt(messageIdParts[1]);
        String title = intent.getStringExtra("TRACK TITLE");
        String[] titleparts = title.split(":");
        updateTrackTitle.setText(titleparts[1]);
        String singer = intent.getStringExtra("TRACK SINGER");
        String[] singerparts = singer.split(":");
        updateTrackSinger.setText(singerparts[1]);

        //A partir de los datos recogidos de id, titulo y cantante hacemos un nuevo track
        updateTrack = new Track(id, title, singer);

        //Inicializamos la conexión con API
        myapirest = APIRest.createAPIRest();

        //Evento boton update
        updateTrackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Justo al clickar al botón, ponemos el spinner de cargando
                progressDialog = new ProgressDialog(UpdateTrackActivity.this);
                progressDialog.setTitle("Loading...");
                progressDialog.setMessage("Waiting for the server");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();

                //Llamar función update track
                updateTrack(updateTrack);
            }
        });
    }

    //Función update track
    private void updateTrack(Track updateTrack) {
        // Desarrollamos la función declarada en la interficie (APIRest)
        Call<Void> trackCall = myapirest.updateTrack(updateTrack);
        trackCall.enqueue(new Callback<Void>() {
            // Recogemos la información que nos da la API (ON RESPONSE: Conexión la API OK)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Si recogemos correctamente la información
                if(response.isSuccessful()){
                    // Como ya hemos obtenido la información podemos cerrar el cargando...
                    progressDialog.hide();
                }
                // Si no recogemos correctamente la información
                else{
                    Log.e("No api connection", response.message());

                    // Al no obtener la información podemos cerrar el cargando...
                    progressDialog.hide();

                    // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateTrackActivity.this);

                    alertDialogBuilder
                            .setTitle("Error")
                            .setMessage(response.message())
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> finish());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            // ON FAILURE: Conexión con la API: KO
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("No api connection: ", t.getMessage());

                // Al no conectarse con la API podemos cerrar el cargando...
                progressDialog.hide();

                // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateTrackActivity.this);

                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> finish());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }
}
