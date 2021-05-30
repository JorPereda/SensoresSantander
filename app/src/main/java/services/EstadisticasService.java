package services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import baseDeDatos.Medidas;
import baseDeDatos.MedidasController;
import datos.Parent;
import datos.SensorAmbiental;
import utilities.HttpHandler;
import utilities.TinyDB;

public class EstadisticasService extends Service {

    public static final String ACTION = "services.EstadisticasService";

    public Context context;
    private ArrayList<Parent> parents = new ArrayList<>();
    //private SensorAmbiental sensor = new SensorAmbiental();
    private MedidasController medidasController;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        Log.d("EstadisticasService ", "OnCreate: antes de operaciones");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(2077, new Notification());

        medidasController = new MedidasController(context);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "sensores.estadisticas";
        String channelName = "Background Service Statistics";
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
        startForeground(2077, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //parents = (ArrayList<Parent>) intent.getSerializableExtra("parents");

        Log.d("EstadisticasService ", "StartCommand: antes de operaciones");

        startTimer();

        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startTimer() {
        Log.d("EstadisticasService ", "StartTimer: antes de operaciones");

        //Intervalo 0 -> horas   1 -> dias
        final int[] intervaloMuestreo = new int[1];
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                TinyDB tinydb = new TinyDB(getBaseContext());
                parents = tinydb.getListParent("parents");
                for(Parent p : parents) {
                    for (SensorAmbiental sensor : p.getChildren()) {
                        Log.d("EstadisticasService", "SensorTimer: " + "La ejecucion entra en el bucle de sensores");
                        intervaloMuestreo[0] = sensor.getIntervaloStatsMuestreo();
                        updateSensor(sensor);
                        recogeStats(sensor);
                        Log.d("EstadisticasService ", "SensorTimer: " + sensor.getIdentificador() + " " + sensor.getTitulo() + " Temp: " + sensor.getTemperatura());
                    }
                }
            }
        };
        if(intervaloMuestreo[0] ==0){
            timer.schedule(timerTask, 0, 300000); //5 min
            //timer.schedule(timerTask, 1000, 10000); //1 minuto
            //Log.d("EstadisticasService ", "Muestreo del sensor " + sensor.getIdentificador() + " " + sensor.getTitulo());
        }
        if(intervaloMuestreo[0] ==1){
            timer.schedule(timerTask, 0, 3600000); //1 hora
            //Log.d("EstadisticasService ", "Muestreo del sensor 1 hora " + sensor.getIdentificador() + " " + sensor.getTitulo());

        }
        if(intervaloMuestreo[0] ==2){
            timer.schedule(timerTask, 0, 86400000); //1 dia
        }


        Intent inResult = new Intent(ACTION);
        inResult.putExtra("resultCodeStats", Activity.RESULT_OK);
        inResult.putExtra("parentsResult", parents);
        LocalBroadcastManager.getInstance(this).sendBroadcast(inResult);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void recogeStats(SensorAmbiental sensor){
        LocalDateTime currentTime = LocalDateTime.now();
        String fechaCortada = currentTime.toString().substring(0,13);
        Medidas medidaNueva = new Medidas(Integer.valueOf(sensor.getIdentificador()), String.valueOf(currentTime), fechaCortada, sensor.getTemperatura(), sensor.getRuido(), sensor.getLuminosidad());
        long id = medidasController.nuevaMedida(medidaNueva);
        if (id == -1) {
            // De alguna manera ocurrió un error
            //Toast.makeText(context, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
            Log.d("EstadisticasService ", "Error al guardar. Intenta de nuevo");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestarterStats.class);
        this.sendBroadcast(broadcastIntent);
    }

    private void updateSensor(SensorAmbiental sensor){
        HttpHandler sh = new HttpHandler();

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

                    /*for(Parent p : parents){
                        ArrayList<SensorAmbiental> listaActualizada = p.getChildren();
                        for(int i = 0; i < p.getChildren().size(); i++){
                            if(p.getChild(i).getIdentificador().equals(sensor.getIdentificador())){
                                listaActualizada.set(i, sensor);
                                p.setChildren(listaActualizada);
                            }
                        }
                    }*/
                } catch (final JSONException e) {
                    Log.e(tag, "Json parsing error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Log.e(tag, "IOException: " + e.getMessage());
        } catch (Exception ex) {
            Log.e(tag, "Exception: " + ex.getMessage());
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
