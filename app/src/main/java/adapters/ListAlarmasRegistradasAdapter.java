package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sensorsantander.R;

import java.util.ArrayList;

import datos.AlarmaRegistrada;

public class ListAlarmasRegistradasAdapter extends ArrayAdapter<AlarmaRegistrada> {
    public ListAlarmasRegistradasAdapter(Context context, ArrayList<AlarmaRegistrada> alarmasRegistradas) {
        super(context, 0, alarmasRegistradas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AlarmaRegistrada alarma = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lista_alarmas_activadas, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.alarma_activada_nombre);
        //TextView tvDate = convertView.findViewById(R.id.alarma_activada_fecha);
        // Populate the data into the template view using the data object
        //tvName.setText(alarma.getValor().toString());
        //tvDate.setText(alarma.getFecha());
        // Return the completed view to render on screen
        return convertView;
    }
}
