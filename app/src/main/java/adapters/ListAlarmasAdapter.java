package adapters;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaAlarmas;

import java.util.ArrayList;

import datos.Alarma;
import utilities.CardViewManage;
import utilities.Interfaces_MVP;

public class ListAlarmasAdapter extends BaseAdapter {

    private ArrayList<Alarma> listaAlarmas;
    private LayoutInflater inflater;
    private Interfaces_MVP.ViewFavoritosYAlarma mView;

    public ListAlarmasAdapter(ArrayList<Alarma> lista, Interfaces_MVP.ViewFavoritosYAlarma view){
        inflater = LayoutInflater.from(view.getActivityContext());
        this.listaAlarmas = lista;
        mView = view;
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
        final ConstraintLayout hiddenView;
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

        return convertView;
    }

    public ArrayList<Alarma> getListAlarmas(){
        return listaAlarmas;
    }

    public void updateListAlarmas(ArrayList<Alarma> alarmas){
        listaAlarmas = alarmas;
    }

    private void addNotification() {
        NotificationManager notif=(NotificationManager)mView.getActivityContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (mView.getActivityContext()).setContentTitle("Titulo").setContentText("body").
                setContentTitle("Titulo").setSmallIcon(R.drawable.ic_add_alarm).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);

    }
}
