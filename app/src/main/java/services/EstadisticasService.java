package services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import baseDeDatos.Medidas;
import baseDeDatos.MedidasController;
import datos.Parent;
import datos.SensorAmbiental;

import static services.AlarmsNotifService.ACTION;

public class EstadisticasService extends Service {

    public Context context;
    private SensorAmbiental sensor = new SensorAmbiental();
    //Intervalo 0 -> horas   1 -> dias
    private int intervalo;
    private MedidasController medidasController;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
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

        sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");
        intervalo = intent.getIntExtra("Intervalo",0);
        Log.d("EstadisticasService ", "SensorIntent: " + sensor.getIdentificador() + " " + sensor.getTitulo());


        startTimer();
        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startTimer() {

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                recogeStats(sensor);
            }
        };
        if(intervalo==1){
            //timer.schedule(timerTask, 1000, 3600000); //1 hora
            timer.schedule(timerTask, 1000, 300000); //5 min
        }
        if(intervalo==2){
            timer.schedule(timerTask, 1000, 86400000); //1 dia
        }

        Intent inResult = new Intent(ACTION);
        inResult.putExtra("resultCode", Activity.RESULT_OK);
        //inResult.putExtra("alarmasResult", listaAlarmas);
        LocalBroadcastManager.getInstance(this).sendBroadcast(inResult);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void recogeStats(SensorAmbiental sensor){
        Date currentTime = Calendar.getInstance().getTime();
        Medidas medidaNueva = new Medidas(Integer.valueOf(sensor.getIdentificador()), String.valueOf(currentTime), sensor.getTemperatura(), sensor.getRuido(), sensor.getLuminosidad());
        //Medidas medidaNueva = new Medidas(2077, String.valueOf(currentTime), "8", "8", "8");
        long id = medidasController.nuevaMedida(medidaNueva);
        if (id == -1) {
            // De alguna manera ocurrió un error
            Toast.makeText(context, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestarterStats.class);
        this.sendBroadcast(broadcastIntent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
