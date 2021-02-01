package adapters;


import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sensorsantander.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import datos.Alarma;
import datos.AlarmaRegistrada;
import utilities.CardViewManage;
import utilities.Interfaces_MVP;

public class ListAlarmasAdapter extends BaseAdapter {

    private final ArrayList<Alarma> listaAlarmas;
    private ArrayList<AlarmaRegistrada> alarmasRegistradas;
    private final LayoutInflater inflater;
    private final Interfaces_MVP.ViewFavoritosYAlarma mView;

    public ListAlarmasAdapter(ArrayList<Alarma> listaAlarmas, Interfaces_MVP.ViewFavoritosYAlarma view){
        inflater = LayoutInflater.from(view.getActivityContext());
        this.listaAlarmas = listaAlarmas;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                mView.onStartService();
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
                    CardViewManage.collapse(hiddenView);
                }else if(hiddenView.getVisibility() == View.GONE){
                    CardViewManage.expand(hiddenView);
                }
            }
        });

        //Todas las alarmas registradas del sensor
        alarmasRegistradas = alarma.getAlarmasRegistradas();
        Log.e("Limpieza ", "Alarmas reg: " + alarmasRegistradas.size());

        //Borramos las de 2 dias antes
        alarmasRegistradas.removeAll(listarAlarmasRegistradas(alarmasRegistradas));
        Log.e("Limpieza ", "Alarmas borrar: " + listarAlarmasRegistradas(alarmasRegistradas).size());
        Log.e("Limpieza ", "Alarmas reg despues: " + alarmasRegistradas.size());

        mView.updateListAlarmasRegistradas(alarma, alarmasRegistradas);

        //Listar cada alarma registrada
        hiddenView.removeAllViews();
        for(AlarmaRegistrada al : alarmasRegistradas){
            View line = View.inflate(mView.getActivityContext(), R.layout.lista_alarmas_activadas, null);
            TextView tvName = line.findViewById(R.id.alarma_activada_nombre);
            String textAlarma = "<b>Valor: </b>" + al.getValor().toString() + "  <b>Fecha:</b> " + al.getFecha();
            tvName.setText(Html.fromHtml(textAlarma));
            tvName.setPaddingRelative(32,0,0,0);
            hiddenView.addView(line);
        }

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<AlarmaRegistrada> listarAlarmasRegistradas(ArrayList<AlarmaRegistrada> alarmasRegistradas){

        ArrayList<AlarmaRegistrada> alarmasRegBorradas = new ArrayList<>();

        LocalDate fechaActual = LocalDate.now();
        fechaActual = fechaActual.minusDays(1);

        for (Iterator<AlarmaRegistrada> iterator = alarmasRegistradas.iterator(); iterator.hasNext();) {
            AlarmaRegistrada al = iterator.next();

            LocalDate fechaSensor = LocalDate.parse(al.getFechaReal());

            if(fechaSensor.isBefore(fechaActual)){
                iterator.remove();
                alarmasRegBorradas.add(al);
            }
        }
        return alarmasRegBorradas;
    }
}
