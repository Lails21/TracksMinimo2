package dsa.eetac.upc.edu.tracksminimo2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTrackActivity extends AppCompatActivity {
    //Declarar textview y botones que aparecen en el layout para pasar valor
    private TextView newTrackID;
    private TextView newTrackTitle;
    private TextView newTrackSinger;
    private Button createNewTrackbtn;
    //Crear nueva Track (donde le meteremos los valores que pase el usuario
    public Track newTrack;
    //Declarar API
    private APIRest myapirest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Definimos el nombre del layout que debe abrirse con esta clase
        setContentView(R.layout.create_track);

        // Identificamos con el nombre que tengan los TextView/Buttons del xml (create_track
        newTrackID = findViewById(R.id.update_track_id);
        newTrackTitle = findViewById(R.id.new_track_title);
        newTrackSinger = findViewById(R.id.new_track_singer);
        createNewTrackbtn = findViewById(R.id.create_track_btn);

        // Abrimos la conexión con la API (siempre antes de cualquier función que necesite su información
        myapirest = APIRest.createAPIRest();

        //Ponemos evento al clickar el boton
        createNewTrackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se recogen los diversos datos de una cancion (titulo, id y cantante) y se guardan en variables
                int id = Integer.parseInt(newTrackID.getText().toString());
                String title = newTrackTitle.getText().toString();
                String singer = newTrackSinger.getText().toString();

                // Se crea la nueva canción con los valores introducidos por el usuario
                newTrack = new Track(id, title, singer);
                // Llamamos a la función para añadir la nueva Track a la lista
                createNewTrack(newTrack);
            }
        });
    }
    // Función para crear una nueva Track y añadirla
    private void createNewTrack(Track newTrack) {
        // Desarrollamos la función declarada en la interficie (APIRest)
        Call<Track> trackCall = myapirest.createTrack(newTrack);
        trackCall.enqueue(new Callback<Track>() {
            // Recogemos la información que nos da la API (ON RESPONSE: Conexión la API OK)
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                // Si recogemos correctamente la información
                if(response.isSuccessful()){
                    finish();
                }
                // Si no recogemos correctamente la información
                else{
                    Log.e("No api connection", response.message());

                    // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateTrackActivity.this);

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
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e("No api connection: ", t.getMessage());

                // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateTrackActivity.this);

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

