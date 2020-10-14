package utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import datos.SensorAmbiental;

public class CheckSensorAlarm extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        final SensorAmbiental sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");
        this.context = context;
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new NotificationTask(sensor).execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 6000);//Cada minuto
    }


    class NotificationTask extends AsyncTask<Void, Void, Void> {

        private static final String tag = "CheckSensorAlarm";
        private SensorAmbiental sensor;
        private int responseCode = -1;
        private String jsonStr;

        public NotificationTask(SensorAmbiental sensorAmbiental) {
            this.sensor = sensorAmbiental;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... strings) {

            HttpHandler sh = new HttpHandler();

            String url = sensor.getUri();
            jsonStr = sh.makeServiceCall(url);
            try {
                URL mUrl = new URL(url);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.connect();
                responseCode = httpConnection.getResponseCode();
            } catch (IOException e) {
                Log.e(tag, "IOException: " + e.getMessage());
            } catch (Exception ex) {
                Log.e(tag, "Exception: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //HTTP: 200
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.e(tag, "Sensor unico para alarma cogido from url: " + jsonStr);
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

                        sensor.setRuido(ruido);
                        sensor.setLuminosidad(luminosidad);
                        sensor.setTemperatura(temperatura);
                        sensor.setUltModificacion(ultMod);
                        Log.e(tag, "Comprobacion de datos del sensor: - Ruido: " + jsonObj.getString("ayto:noise"));
                        Log.e(tag, "Comprobacion de datos del sensor: - Luz: " + jsonObj.getString("ayto:light"));
                        Log.e(tag, "Comprobacion de datos del sensor: - Temp: " + jsonObj.getString("ayto:temperature"));

                    }
                } catch (final JSONException e) {
                    Log.e(tag, "Json parsing error: " + e.getMessage());
                }
            }
        }
    }
}

