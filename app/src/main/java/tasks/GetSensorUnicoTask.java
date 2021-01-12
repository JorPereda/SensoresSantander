package tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import datos.Alarma;
import datos.SensorAmbiental;
import utilities.HttpHandler;
import utilities.Interfaces_MVP;

public class GetSensorUnicoTask extends AsyncTask<Void, Void, SensorAmbiental> {

    private static final String tag = "CheckSensorAlarm";
    private SensorAmbiental sensor;
    private Alarma alarma;
    private ArrayList<Alarma> listaAlarmas;
    private Interfaces_MVP.ViewFavoritosYAlarma mView;

    public GetSensorUnicoTask(Alarma alarma, ArrayList<Alarma> listaAlarmas, Interfaces_MVP.ViewFavoritosYAlarma mView) {
        this.alarma = alarma;
        this.listaAlarmas = listaAlarmas;
        this.mView = mView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sensor = alarma.getSensor();
    }

    @Override
    protected SensorAmbiental doInBackground(Void... strings) {

        HttpHandler sh = new HttpHandler();

        String url = sensor.getUri();
        String jsonStr = sh.makeServiceCall(url);
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
        return sensor;
    }

    @Override
    protected void onPostExecute(SensorAmbiental sensor) {
        super.onPostExecute(sensor);
        int id = alarma.getIdAlarma();
        for(int i=0; i<listaAlarmas.size(); i++){
            Alarma a = listaAlarmas.get(i);
            if(a.getIdAlarma()==id){
                a.setSensor(sensor);
            }
        }
        mView.updateListAlarmas(listaAlarmas);
        //listaAlarmas.get(alarma.getIdAlarma()).setSensor(sensor);

    }
}


