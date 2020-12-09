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

import datos.Parent;
import datos.SensorAmbiental;
import utilities.HttpHandler;
import utilities.Interfaces_MVP;

public class UpdateFavoritosTask extends AsyncTask<Void, Void, ArrayList<Parent>> {

    private final Interfaces_MVP.ViewFavoritosYAlarma mView;
    private ArrayList<Parent> parents;
    private ArrayList<SensorAmbiental> sensores;


    public UpdateFavoritosTask(ArrayList<Parent> parents, Interfaces_MVP.ViewFavoritosYAlarma mView) {
        this.parents = parents;
        this.mView = mView;
        Log.d("Task parents: ", parents.toString());

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Parent> doInBackground(Void... voids) {
        Log.d("TaskBack parents: ", parents.toString());

        HttpHandler sh = new HttpHandler();
        String tag = "Task update favoritos";

        for (Parent p : parents){
            sensores = p.getChildren();
            for (SensorAmbiental sensor : sensores){
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

                                sensor.setRuido(ruido);
                                sensor.setLuminosidad(luminosidad);
                                sensor.setTemperatura(temperatura);
                                sensor.setUltModificacion(ultMod);
                                Log.e(tag, "Comprobacion de datos del sensor: ID: " + sensor.getIdentificador());
                                Log.e(tag, "- Ruido: " + ruido);
                                Log.e(tag, "- Luz: " + luminosidad);
                                Log.e(tag, "- Temp: " + temperatura);
                                Log.e(tag, "Uri: " + sensor.getUri());

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

            }
        }

        return parents;
    }

    @Override
    protected void onPostExecute(ArrayList<Parent> parents) {
        mView.updateListParents(parents);
        //mView.updateListView();
    }
}
