package dsa.eetac.upc.edu.tracksminimo2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIRest {

    //We specify the URL of the API
    String BASE_URL = "http://147.83.7.155:8080/dsaApp/";

    //Get all tracks --> Función que da la lista con las diversas canciones
    @GET("tracks")
    Call<List<Track>> getAllTracks();

    //Get an especific track passing its id
    @GET("tracks/{id}")
    Call<Track> getTrack(@Path("id") int id);

    //Create a new track, creamos una track nueva, le pasamos BODY porque contiene (id, titulo, cantante)
    @POST("tracks")
    Call<Track> createTrack(@Body Track track);

    //Update a track
    @PUT("tracks")
    Call<Void> updateTrack(@Body Track track);

    //Delete a track --> Solo necesitamos el id del Track que queremos eliminar
    @DELETE("tracks/{id}")
    Call<Void> deleteTrack(@Path("id") int id);

    // Creamos la conexión con la API
    static APIRest createAPIRest() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(APIRest.class);
    }
}

