package adapters;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaAlarmas;

import java.util.ArrayList;

import datos.Alarma;
import datos.AlarmaRegistrada;
import utilities.CardViewManage;
import utilities.Interfaces_MVP;

public class ListAlarmasAdapter extends BaseAdapter {

    private ArrayList<Alarma> listaAlarmas;
    private ArrayList<AlarmaRegistrada> alarmasRegistradas;
    private LayoutInflater inflater;
    private Interfaces_MVP.ViewFavoritosYAlarma mView;

    private String nombreAlarmaNotificacion;

    public ListAlarmasAdapter(ArrayList<Alarma> listaAlarmas, String nombreAlarmaNotificacion, Interfaces_MVP.ViewFavoritosYAlarma view){
        inflater = LayoutInflater.from(view.getActivityContext());
        this.listaAlarmas = listaAlarmas;
        mView = view;
        this.nombreAlarmaNotificacion = nombreAlarmaNotificacion;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.card_alarmas, parent, false);


        final Alarma alarma = listaAlarmas.get(position);


        TextView tvAlarmName = convertView.findViewById(R.id.tv_alarm_name);
        TextView tvAlarmMedidaLabel = convertView.findViewById(R.id.tv_alarm_medidalabel);
        TextView tvAlarmMedida = convertView.findViewById(R.id.tv_alarm_medida);
        //TextView tvAlarmMedidaActual = convertView.findViewById(R.id.tv_alarm_medida_actual);
        ImageButton borrarAlarma = convertView.findViewById(R.id.ib_alarm_borrar);

        tvAlarmName.setText(alarma.getNombre());
        //tvAlarmMedidaLabel.setText(alarma.getTipoAlarma());
        tvAlarmMedida.setText(String.valueOf(alarma.getValorAlarma()));

        String tipo = alarma.getTipoAlarma();
        String maxMin = alarma.getMaxMin();
        if(tipo.equals("temp")){
            if(maxMin.equals("max")){
                tvAlarmMedidaLabel.setText("Temp. max: ");
            }else{
                tvAlarmMedidaLabel.setText("Temp. min: ");
            }
        }else if(tipo.equals("luz")){
            if(maxMin.equals("max")){
                tvAlarmMedidaLabel.setText("Luz max: ");
            }else{
                tvAlarmMedidaLabel.setText("Luz. min: ");
            }
        }else if(tipo.equals("ruido")){
            if(maxMin.equals("max")){
                tvAlarmMedidaLabel.setText("Ruido max: ");
            }else{
                tvAlarmMedidaLabel.setText("Ruido. min: ");
            }
        }

        borrarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaAlarmas.remove(alarma);
                notifyDataSetChanged();
                mView.updateListAlarmas(listaAlarmas);
            }
        });

        //Parte desplegable de la card view
        final LinearLayout hiddenView;
        hiddenView = convertView.findViewById(R.id.hidden_alarmas_view);
        final CardViewManage cardViewManage = new CardViewManage(mView.getActivityContext());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenView.getVisibility() == View.VISIBLE) {
                    cardViewManage.collapse(hiddenView);
                }else if(hiddenView.getVisibility() == View.GONE){
                    cardViewManage.expand(hiddenView);
                }
            }
        });

        alarmasRegistradas = alarma.getAlarmasRegistradas();
        for(AlarmaRegistrada al : alarmasRegistradas){
            View line = hiddenView.inflate(mView.getActivityContext(), R.layout.lista_alarmas_activadas, null);
            TextView tvName = line.findViewById(R.id.alarma_activada_nombre);
            //TextView tvFecha = line.findViewById(R.id.alarma_activada_fecha);
            String textAlarma = "<b>Valor: </b>" + al.getValor().toString() + "  <b>Fecha:</b> " + al.getFecha();
            tvName.setText(Html.fromHtml(textAlarma));
            //tvFecha.setText(al.getFecha());
            tvName.setPaddingRelative(32,0,0,0);
            //tvFecha.setPaddingRelative(0,0,0,0);
            hiddenView.addView(line);
        }

        /*if (nombreAlarmaNotificacion.equals(alarma.getNombre())){
            hiddenView.setVisibility(View.VISIBLE);
        }*/


        return convertView;
    }
}
