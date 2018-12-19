package dsa.eetac.upc.edu.tracksminimo2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SingleTrackDialog.SingleTrackDialogListener{

    //Declarar API
    private APIRest myapirest;
    //Declarar/Crear el RecyclerView y decir que la clase Recycle será quien lo gestionará
    private Recycler recycler;
    private RecyclerView recyclerView;
    //Declaramos el spinner de cargando en el Activity donde estamos esperando los datos
    ProgressDialog progressDialog;
    //Declarar token (2 funciones al final)
    private String token;
    // Declaramos los TextViews y los buttons que aparecen en el layout
    private TextView idTrack;
    private TextView titleTrack;
    private TextView singerTrack;
    private Button getAllTracks;
    private Button getSingleTrack;
    private Button createTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Definimos el nombre del layout que debe abrirse con esta clase
        setContentView(R.layout.activity_main);

        // Identificamos con el nombre que tenga el RecyclerView en el xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Definimos ciertos valores de estructura del RecyclerView
        recycler = new Recycler(this);
        recyclerView.setAdapter(recycler);
        recyclerView.setHasFixedSize(true);
        // Le asignamos a cada linea del RecyclerView el LinearLayout de itemtrack
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Identificamos con el nombre que tengan los TextView/buttons del xml (activity_main)
        idTrack = findViewById(R.id.idTrack);
        titleTrack = findViewById(R.id.titleTrack);
        singerTrack = findViewById(R.id.singerTrack);
        getAllTracks = findViewById(R.id.getAllTracksbtn);
        getSingleTrack = findViewById(R.id.getSingleTrackbtn);
        createTrack = findViewById(R.id.createTrackbtn);

        // Justo al abrir esta actividad ponemos el spinner de cargando
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Waiting for the server");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        // Abrimos la conexión con la API (siempre antes de cualquier función que necesite su información
        myapirest = APIRest.createAPIRest();

        // Llamamos a las funciones que recogeran información de la API
        getAllTracks();

        // Definimos las funciones que se ejecutarán al dar Click a los tres botones
        getSingleTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            // Llama a la función que abre una ventanita para darte la información de ese Track
            public void onClick(View v) {
                openDialog();
            }
        });

        getAllTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            // Llama a la función getAllTrack (solo actualiza), no muestra ni abre nada
            public void onClick(View v) {
                getAllTracks();
            }
        });

        createTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            // Llama a la función que abre un nuevo Activity
            public void onClick(View v) {
                openCreateLayout();
            }
        });

    }

    // Abre un nuevo Activity
    private void openCreateLayout(){
        Intent intent = new Intent(this, CreateTrackActivity.class);
        startActivity(intent);
    }

    //A bre una ventanita para darte la información de ese Track
    private void openDialog() {
        SingleTrackDialog  singleTrackDialog= new SingleTrackDialog();
        singleTrackDialog.show(getSupportFragmentManager(), "Single Track Dialog");
    }

    @Override
    public void applyTexts(int id){
        getSingleTrack(id);
    }

    //Función que te da la lista de tracks
    public void getAllTracks() {
        // Desarrollamos la función declarada en la interficie (APIRest)

        Call<List<Track>> trackCall = myapirest.getAllTracks();
        trackCall.enqueue(new Callback<List<Track>>() {
            // Recogemos la información que nos da la API (ON RESPONSE: Conexión la API OK)
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                // Si recogemos correctamente la información
                if(response.isSuccessful()){
                    // Metemos los campos que nos vengan de la API en una lista de Tracks (body porque tiene más de un campo)
                    List<Track> tracksList = response.body();
                    // Metemos en el recycler la lista
                    if(tracksList.size() != 0){
                        recycler.clear();
                        recycler.addTracks(tracksList);
                    }
                    // Como ya hemos obtenido la información podemos cerrar el cargando...
                    progressDialog.hide();

                    for(int i = 0; i < tracksList.size(); i++) {
                        Log.i("Track id: " + tracksList.get(i).id, response.message());
                    }
                    Log.i("Size of the list: " +tracksList.size(), response.message());
                }
                // Si no recogemos correctamente la información
                else{
                    Log.e("No api connection", response.message());
                    // Al no obtener la información podemos cerrar el cargando...
                    progressDialog.hide();

                    // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    alertDialogBuilder
                            .setTitle("Error")
                            .setMessage(response.message())
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
            // ON FAILURE: Conexión con la API: KO
            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e("No api connection: ", t.getMessage());
                // Al no conectarse con la API podemos cerrar el cargando...
                progressDialog.hide();

                // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    // Función para recoger los datos de un solo Track
    private void getSingleTrack (int id){
        // Desarrollamos la función declarada en la interficie (APIRest)
        Call<Track> trackCall = myapirest.getTrack(id);
        trackCall.enqueue(new Callback<Track>() {
            // Recogemos la información que nos da la API (ON RESPONSE: Conexión la API OK)
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                // Si recogemos correctamente la información
                if(response.isSuccessful()){
                    // Metemos los campos que nos vengan de la API en una estructura de Track (body porque tiene más de un campo)
                    Track track = response.body();
                    // Metemos en el recycler la lista
                    if(recycler.getItemCount() != 0){
                        recycler.clear();
                        recycler.addSingleTrack(track);
                    }

                    Log.i("Single Track id: " +track.id, response.message());
                    // Como ya hemos obtenido la información podemos cerrar el cargando...
                    progressDialog.hide();
                }
                // Si no recogemos correctamente la información
                else{
                    Log.e("No api connection", response.message());
                    // Al no obtener la información podemos cerrar el cargando...
                    progressDialog.hide();

                    // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    alertDialogBuilder
                            .setTitle("Error")
                            .setMessage(response.message())
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            // ON FAILURE: Conexión con la API: KO
            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e("No api connection: ", t.getMessage());
                // Al no conectarse con la API podemos cerrar el cargando...
                progressDialog.hide();

                // Le mostramos un mensaje de error al usuario para que no pete la aplicación
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (token != null) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            token = data.getStringExtra("token");
        }
    }
}
