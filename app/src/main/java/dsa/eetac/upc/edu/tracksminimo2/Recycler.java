package dsa.eetac.upc.edu.tracksminimo2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recycler extends RecyclerView.Adapter<Recycler.ViewHolder> {

    //Recojer el texto del TextView al clickarle boton
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //Creamos lista con la estructura de track para recoger todos los tracks. Llamarlo data
    private List<Track> data;
    //Necesario para el constructor del recycler
    private Context context;
    //Declarar API
    private APIRest myapirest;

    // Función para añadir a la lista de data la lista recogida de la API
    public void addTracks(List<Track> tracksList) {
        data.addAll(tracksList);
        notifyDataSetChanged();
    }

    // Función para añadir la Track en data a partir de la información recogida de la API
    public void addSingleTrack(Track track) {
        data.add(track);
    }

    //Gestionamos el RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Creamos unos TextView, un Button y un LinearLayout
        private LinearLayout linearLayout;
        private TextView idTrackView;
        private TextView titleTrackView;
        private TextView singerTrackView;
        private Button deletebtn;

        public ViewHolder(View v) {
            super(v);
            // Los identificamos con el nombre que tengan en el xml
            idTrackView = v.findViewById(R.id.idTrack);
            titleTrackView = v.findViewById(R.id.titleTrack);
            singerTrackView = v.findViewById(R.id.singerTrack);
            deletebtn = v.findViewById(R.id.deleteTrackbtn);
            linearLayout = v.findViewById(R.id.linearLayout);
        }
    }

    //Constructor (utilizar context)
    public Recycler(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        myapirest = APIRest.createAPIRest();
    }

    // Inflamos el RecyclerView con las filas del item_track
    @Override
    public Recycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new ViewHolder(v);
    }

    // Rellenamos el RecyclerView con toda la información que tenemos en la lista de Data
    @Override
    public void onBindViewHolder(Recycler.ViewHolder holder, int position) {
        // Creamos una variable Track y vamos guardando la información de cada posición de la lista Data
        Track trackData = data.get(position);
        // Cogemos la información de la API y se la pasamos a los TextView de cada linea del RecyclerView
        holder.idTrackView.setText("ID: " + String.valueOf(trackData.id));
        holder.titleTrackView.setText("Title: " +trackData.title);
        holder.singerTrackView.setText("Singer: " +trackData.singer);

        //Evento del boton para borrar una track (no está implementado)
        holder.deletebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteTrack(trackData.id);
            }
        });

        //Si clickamos una fila del recycler nos lleva a otro layout para editar titulo o cantante
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un intent para abrir el nuevo activity, le ponemos el nombre de la clase que queremos abrir
                Intent intent = new Intent(context, UpdateTrackActivity.class);
                //Creamos y asignamos variable de de los diferents textviews
                TextView textId = v.findViewById(R.id.idTrack);
                TextView textTitle = v.findViewById(R.id.titleTrack);
                TextView textSinger = v.findViewById(R.id.singerTrack);
                //Guardamos el valor id del editText una variable tipo String
                String messageId = textId.getText().toString();
                //Trozear
                String[] messageIdparts = messageId.split(":");
                String id = messageIdparts[1];
                //Guardamos el valor del titulo y cantante editText una variable tipo String
                String messageTitle = textTitle.getText().toString();
                String messageSinger = textSinger.getText().toString();
                //Para pasar el string de un a otro activity se lo defines de esta manera (EXTRAMESSAGE)
                intent.putExtra("TRACK ID", id);
                intent.putExtra("TRACK TITLE", messageTitle);
                intent.putExtra("TRACK SINGER", messageSinger);
                //Se abre la nueva actividad
                context.startActivity(intent);
            }
        });
    }

    //Funcion eliminar una Track
    private void deleteTrack(int id) {
        // Desarrollamos la función declarada en la interficie (APIRest)
        Call<Void> trackCall = myapirest.deleteTrack(id);

        trackCall.enqueue(new Callback<Void>() {
            // Recogemos la información que nos da la API (ON RESPONSE: Conexión la API OK)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Si recogemos correctamente la información
                if(response.isSuccessful()){
                    Toast.makeText(context, "Song with ID: " +id + " deleted", Toast.LENGTH_LONG).show();
                }
                // Si no recogemos correctamente la información
                else{

                }
            }

            // ON FAILURE: Conexión con la API: KO
            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void clear(){
        final int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Devuelve el tamaño de la lista de datos
    @Override
    public int getItemCount() {
        return data.size();
    }
}

