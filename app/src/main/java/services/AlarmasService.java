package services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import datos.Alarma;
import datos.SensorAmbiental;
import tasks.CompruebaAlarmaTask;
import tasks.GetSensorUnicoTask;
import tasks.PruebaTask;
import utilities.Interfaces_MVP;

import static android.content.ContentValues.TAG;

public class AlarmasService extends Service {

    private Interfaces_MVP.ViewFavoritosYAlarma mView;
    private Context context;
    private final IBinder mBinder = new LocalBinder();
    //private Alarma alarma;
    private ArrayList<Alarma> listaAlarmas;

    public class LocalBinder extends Binder {
        public AlarmasService getService() {
            Log.d(TAG, "getService()");
            return AlarmasService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        //alarma = (Alarma) intent.getSerializableExtra("alarma");
        //mView = (Interfaces_MVP.ViewFavoritosYAlarma) intent.getSerializableExtra("context");

        //new CompruebaAlarmaTask(alarma, (Interfaces_MVP.ViewFavoritosYAlarma) context).execute();

        //Log.d(TAG, "Mensaje de prueba mView en AlarmasService: ");
        context = this;

        //alarma = (Alarma) intent.getSerializableExtra("alarma");
        listaAlarmas = (ArrayList<Alarma>) intent.getSerializableExtra("alarmas");

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        for(Alarma alarma : listaAlarmas){
                            //new PruebaTask(context).execute();
                            new CompruebaAlarmaTask(context, alarma).execute();
                            //Log.d(TAG, "Mensaje de prueba en Task nueva de Service. " + alarma.getNombre());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 60000);

        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }
}
