package services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
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
import utilities.HttpHandler;
import utilities.TinyDB;

import static services.AlarmsNotifService.ACTION;

public class AlarmasKeepRunningService extends Service {

    public Context context;
    public ArrayList<Alarma> listaAlarmas;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(2077, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "sensores.alarmas";
        String channelName = "Background Service Alarms";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2000, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //if(listaAlarmas==null){
            listaAlarmas = (ArrayList<Alarma>) intent.getSerializableExtra("alarmas");
        //}

        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestarterAlarmas.class);
        this.sendBroadcast(broadcastIntent);
    }



    private Timer timer;
    private TimerTask timerTask;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startTimer() {

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                compruebaAlarma(listaAlarmas);
            }
        };
        timer.schedule(timerTask, 1000, 300000); //5 minutos

        Intent inResult = new Intent(ACTION);
        inResult.putExtra("resultCode", Activity.RESULT_OK);
        inResult.putExtra("alarmasResult", listaAlarmas);
        LocalBroadcastManager.getInstance(this).sendBroadcast(inResult);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void compruebaAlarma(ArrayList<Alarma> listaAlarmas){

        for(Alarma alarma : listaAlarmas){
            alarma = updateSensor(alarma);

            SensorAmbiental sensor = alarma.getSensor();
            String tipo = alarma.getTipoAlarma();
            String maxMin = alarma.getMaxMin();
            Double valorAlarma = alarma.getValorAlarma();
            Double valorSensor = 0.0;
            Boolean estadoAlarma = alarma.getSaltaAlarma();
            Boolean saltaAlarma = false;
            ArrayList<AlarmaRegistrada> alarmasRegistradas = new ArrayList<>();
            alarmasRegistradas = alarma.getAlarmasRegistradas();


            Log.i("Alarma Service", "Estado de alarma 1: " + saltaAlarma.toString());


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

            Log.i("Alarma Service", "Estado de alarma 2: " + saltaAlarma.toString());


            //Si la comprobacion resulta true, genera notificacion
            if(estadoAlarma){
                if (saltaAlarma==false){
                    alarma.setSaltaAlarma(false);
                }
            }

            if (estadoAlarma==false){
                if (saltaAlarma){
                    alarma.setSaltaAlarma(true);
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

                    AlarmaRegistrada alarmaRegistrada = new AlarmaRegistrada(valorSensor, fecha);

                    //Añadimos aparte la fecha completa en formato por defecto para comprobaciones
                    LocalDate fechalocal;
                    fechalocal = LocalDate.now();
                    alarmaRegistrada.setFechaReal(fechalocal.toString());

                    alarmasRegistradas.add(alarmaRegistrada);
                    alarma.setAlarmasRegistradas(alarmasRegistradas);

                    TinyDB tinydb = new TinyDB(this);
                    tinydb.putListAlarmas("alarmas", listaAlarmas);

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
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

                    //Crear notificacion
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.sensoricon)
                            .setGroup("Alarmas de sensor")
                            .setContentTitle(alarma.getNombre())
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("La alarma " + alarma.getNombre() + " ha sido activada con un valor de: " + valorSensor + "."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(alarma.getIdAlarma(), builder.build());
                }
            }
        }
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
