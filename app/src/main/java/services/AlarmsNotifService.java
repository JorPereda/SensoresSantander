package services;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaAlarmas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import datos.Alarma;
import datos.AlarmaRegistrada;
import datos.SensorAmbiental;
import tasks.GetSensorUnicoTask;
import tasks.UpdateFavoritosTask;
import utilities.HttpHandler;
import utilities.Interfaces_MVP;
import utilities.TinyDB;

public class AlarmsNotifService extends IntentService {

    public static final String ACTION = "services.AlarmsNotifService";

    private Context context;
    //private Alarma alarma;
    private ArrayList<Alarma> listaAlarmas;

    public AlarmsNotifService() {
        super("AlarmsNotifService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        listaAlarmas = (ArrayList<Alarma>) intent.getSerializableExtra("alarmas");

        /*if(listaAlarmas.size()!=0){
            Log.e("Alarmas regs: ", String.valueOf(listaAlarmas.get(0).getAlarmasRegistradas().size()));
            Log.e("Alarmas size: ", String.valueOf(listaAlarmas.size()));
            //Log.e("Primera alarma antes: ", String.valueOf(listaAlarmas.size()));
        }*/

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void run() {
                        for(Alarma al : listaAlarmas){
                            al = updateSensor(al);
                            compruebaAlarma(al);

                            //Log.d("TAG alarma", "Mensaje de prueba en Task nueva de Service. " + al.getNombre());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 6000);


        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);

        //Log.e("Alarma finservice", "Alarmas registradas " + listaAlarmas.get(0).getAlarmasRegistradas().size());

        Intent inResult = new Intent(ACTION);
        inResult.putExtra("resultCode", Activity.RESULT_OK);
        inResult.putExtra("alarmasResult", listaAlarmas);
        LocalBroadcastManager.getInstance(this).sendBroadcast(inResult);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void compruebaAlarma(Alarma alarma){

        SensorAmbiental sensor = alarma.getSensor();
        String tipo = alarma.getTipoAlarma();
        String maxMin = alarma.getMaxMin();
        Double valorAlarma = alarma.getValorAlarma();
        Double valorSensor = 0.0;
        Boolean saltaAlarma = alarma.getSaltaAlarma();
        ArrayList<AlarmaRegistrada> alarmasRegistradas = new ArrayList<>();
        alarmasRegistradas = alarma.getAlarmasRegistradas();


        //Comprobacion de la alarma registrada

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

        //Si la comprobacion resulta true, genera notificacion

        if (saltaAlarma){

            alarma.setSaltaAlarma(false);
            int dia = LocalDateTime.now().getDayOfMonth();
            int mes = LocalDateTime.now().getMonthValue();
            int hora = LocalDateTime.now().getHour();
            int minuto = LocalDateTime.now().getMinute();

            String fecha;

            //Cambios debido a que los numeros menores de 10 salen con una unica cifra
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

            //Log.e("Alarma saltada!!", "Ha saltado la alarma del sensor " + alarma.getNombre() + " con valor " + alarma.getMaxMin() + " = " + valorSensor);
            //Log.e("Alarma saltada(Datos). ", "Hora de la alarma: " + fecha + " con nombre " + alarma.getNombre());

            AlarmaRegistrada alarmaRegistrada = new AlarmaRegistrada(valorSensor, fecha);

            //Añadimos aparte la fecha completa en formato por defecto para comprobaciones
            LocalDate fechalocal;
            fechalocal = LocalDate.now();
            Log.e("Fecha. ", "LocalDate.now: " + fechalocal);
            alarmaRegistrada.setFechaReal(fechalocal.toString());
            Log.e("Fecha. ", "Alarma.getFechaReal: " + alarmaRegistrada.getFechaReal());

            alarmasRegistradas.add(alarmaRegistrada);
            alarma.setAlarmasRegistradas(alarmasRegistradas);

            TinyDB tinydb = new TinyDB(this);
            tinydb.putListAlarmas("alarmas", listaAlarmas);

            Log.e("Alarmas regs: ", String.valueOf(listaAlarmas.get(0).getAlarmasRegistradas().size()));

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
        saltaAlarma = false;

    }

    private Alarma updateSensor(Alarma alarma){
        HttpHandler sh = new HttpHandler();

        SensorAmbiental sensor = alarma.getSensor();

        String url = sensor.getUri();
        String jsonStr = sh.makeServiceCall(url);
        String tag = "Update sensor Service";
        try {
            URL mUrl = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();

            //HTTP: 200
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray sensores = jsonObj.getJSONArray("resources");

                    // looping through All sensors
                    for (int i = 0; i < sensores.length(); i++) {
                        JSONObject s = sensores.getJSONObject(i);

                        String ruido = s.getString("ayto:noise");
                        String luminosidad = s.getString("ayto:light");
                        String temperatura = s.getString("ayto:temperature");
                        String ultMod = s.getString("dc:modified");
                        Log.e(tag, "Sensor unico para alarma cogido from url: " + jsonStr);

                        sensor.setRuido(ruido);
                        sensor.setLuminosidad(luminosidad);
                        sensor.setTemperatura(temperatura);
                        sensor.setUltModificacion(ultMod);
                        Log.e(tag, "Comprobacion de datos del sensor: ID: " + sensor.getIdentificador());
                        Log.e(tag, "- Ruido: " + ruido);
                        Log.e(tag, "- Luz: " + luminosidad);
                        Log.e(tag, "- Temp: " + temperatura);

                    }
                } catch (final JSONException e) {
                    Log.e(tag, "Json parsing error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Log.e(tag, "IOException: " + e.getMessage());
        } catch (Exception ex) {
            Log.e(tag, "Exception: " + ex.getMessage());
        }
        return alarma;
    }


}