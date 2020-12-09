package tasks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaAlarmas;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import datos.Alarma;
import datos.AlarmaRegistrada;
import datos.SensorAmbiental;
import utilities.Interfaces_MVP;

public class CompruebaAlarmaTask extends AsyncTask<Void, Void, Boolean> {

    private Alarma alarma;
    private SensorAmbiental sensor;
    //True si se dan las caracteristicas para que se active la alarma
    private Boolean saltaAlarma;

    //private Interfaces_MVP.ViewFavoritosYAlarma mView;
    private Context context;

    private String tipo;
    private String maxMin;
    private Double valorAlarma;
    private Double valorSensor;

    public CompruebaAlarmaTask(Context context, Alarma alarma){
        this.context = context;
        this.alarma = alarma;
    }

    @Override
    protected void onPreExecute() {
        saltaAlarma = false;
        sensor = alarma.getSensor();
        tipo = alarma.getTipoAlarma();
        maxMin = alarma.getMaxMin();
        valorAlarma = alarma.getValorAlarma();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        if(maxMin.equals("min")){
            switch (tipo){
                case "temp":
                    //Compare to:
                    // <0 : numero1 menor que numero2
                    // >0 : numero1 mayor que numero2
                    if(Double.valueOf(sensor.getTemperatura()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getTemperatura());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
                case "luz":
                    if(Double.valueOf(sensor.getLuminosidad()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getLuminosidad());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
                case "ruido":
                    if(Double.valueOf(sensor.getRuido()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getRuido());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
            }
        }else if(maxMin.equals("max")){
            switch (tipo){
                case "temp":
                    //Compare to:
                    // <0 : numero1 menor que numero2
                    // >0 : numero1 mayor que numero2
                    if(Double.valueOf(sensor.getTemperatura()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getTemperatura());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
                case "luz":
                    if(Double.valueOf(sensor.getLuminosidad()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getLuminosidad());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
                case "ruido":
                    if(Double.valueOf(sensor.getRuido()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                        valorSensor = Double.valueOf(sensor.getRuido());
                    }else{
                        saltaAlarma = false;
                    }
                    break;
            }
        }


        return saltaAlarma;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(Boolean saltaAlarma) {

        ArrayList<AlarmaRegistrada> alarmasRegistradas = alarma.getAlarmasRegistradas();

        Log.d("Alarma prueba. ", saltaAlarma.toString() + " " + alarma.getNombre()
                + " Valor alarma: " + alarma.getValorAlarma()
                + " Valor sensor: " + alarma.getSensor().getTemperatura()
                + " Valor maxmin: " + alarma.getMaxMin()
                + " Valor tipo: " + alarma.getTipoAlarma()
        );


        if (saltaAlarma){

            int dia = LocalDateTime.now().getDayOfMonth();
            int mes = LocalDateTime.now().getMonthValue();
            int hora = LocalDateTime.now().getHour();
            int minuto = LocalDateTime.now().getMinute();

            String fecha;

            if(dia<10){
                if(minuto<10){
                    fecha = "0" + dia + "/" + mes + " " + hora + ":" + "0" + minuto;
                }else{
                    fecha = "0" + dia + "/" + mes + " " + hora + ":" + minuto;
                }
            }else{
                if(minuto<10){
                    fecha = dia + "/" + mes + " " + hora + ":" + "0" + minuto;
                }else{
                    fecha = dia + "/" + mes + " " + hora + ":" + minuto;
                }
            }

            Log.e("Alarma saltada!!", "Ha saltado la alarma del sensor " + alarma.getNombre() + " con valor " + alarma.getMaxMin() + " = " + valorSensor);
            Log.e("Alarma saltada(Datos). ", "Hora de la alarma: " + fecha + " con nombre " + alarma.getNombre());

            AlarmaRegistrada alarmaRegistrada = new AlarmaRegistrada(valorSensor, fecha);

            alarmasRegistradas.add(alarmaRegistrada);
            alarma.setAlarmasRegistradas(alarmasRegistradas);
            //mView.updateAlarmInList(alarma);

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            String CHANNEL_ID = String.valueOf(alarma.getIdAlarma());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Nombre del canal";
                String description = "Descripcion";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }


            Intent i = new Intent(context, VistaAlarmas.class);
            //i.putExtra("nombre", alarma.getNombre());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
            
            //Crear notificacion
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.sensoricon)
                    .setGroup("Alarmas de sensor")
                    .setContentTitle(alarma.getNombre())
                    .setAutoCancel(true)
                    //.setContentText("La alarma " + alarma.getNombre() + " ha sido activada con un valor de: " + valorSensor + ".")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("La alarma " + alarma.getNombre() + " ha sido activada con un valor de: " + valorSensor + "."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(alarma.getIdAlarma(), builder.build());


        }

    }
}
