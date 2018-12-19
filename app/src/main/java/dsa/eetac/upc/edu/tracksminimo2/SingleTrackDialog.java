package dsa.eetac.upc.edu.tracksminimo2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SingleTrackDialog extends AppCompatDialogFragment {
    //Declara EditText que aparece en el layout para pasar el valor
    private EditText trackidtext;
    //Crea/Declara el escuchador
    private SingleTrackDialogListener listener;

    // Creamos el Dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflamos el dialog con el contenido del xml (layout_dialog)
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        // Definimos la forma que tendrá el Dialog
        builder.setView(view)
                // Título del Dialog
                .setTitle("Type a track ID")
                // Botón negativo
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    // Definimos que ocurrirá cuando le clickemos al botón negativo
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                // Botón positivo
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // Definimos que ocurrirá cuando le clickemos al botón positivo
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Guardamos en la variable id, el texto que haya introducido el usuario
                        int id = Integer.parseInt(trackidtext.getText().toString());
                        listener.applyTexts(id);
                    }
                });

        // Identificamos con el nombre que tengan el TextView del xml (layout_dialog)
        trackidtext = view.findViewById(R.id.id_track);

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            listener = (SingleTrackDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface SingleTrackDialogListener{
        void applyTexts(int id);
    }

}

