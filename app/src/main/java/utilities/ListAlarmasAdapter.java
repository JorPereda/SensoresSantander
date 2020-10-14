package utilities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorsantander.R;

import java.util.ArrayList;

import datos.Alarma;

public class ListAlarmasAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Alarma> listaAlarmas;
    private LayoutInflater inflater;


    public ListAlarmasAdapter(Activity activity, ArrayList<Alarma> lista){
        this.activity = activity;
        this.listaAlarmas = lista;
    }

    @Override
    public int getCount() {
        return listaAlarmas.size();
    }

    @Override
    public Object getItem(int position) {
        return listaAlarmas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.card_alarmas, parent, false);


        final Alarma alarma = listaAlarmas.get(position);


        TextView tvAlarmName = convertView.findViewById(R.id.tv_alarm_name);
        TextView tvAlarmMedidaLabel = convertView.findViewById(R.id.tv_alarm_medidalabel);
        TextView tvAlarmMedida = convertView.findViewById(R.id.tv_alarm_medida);
        TextView tvAlarmMedidaActual = convertView.findViewById(R.id.tv_alarm_medida_actual);
        ImageButton borrarAlarma = convertView.findViewById(R.id.ib_alarm_borrar);

        tvAlarmName.setText(alarma.getNombre());
        tvAlarmMedidaLabel.setText(alarma.getTipoAlarma());
        tvAlarmMedida.setText(String.valueOf(alarma.getValorAlarma()));

        String tipo = alarma.getTipoAlarma();
        if(tipo.equals("Temp. max: ")){
            tvAlarmMedidaActual.setText((alarma.getSensor().getTemperatura()));
        }else if(tipo.equals("Luz max: ")){
            tvAlarmMedidaActual.setText((alarma.getSensor().getLuminosidad()));
        }else if(tipo.equals("Ruido max: ")){
            tvAlarmMedidaActual.setText((alarma.getSensor().getRuido()));
        }

        borrarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}
