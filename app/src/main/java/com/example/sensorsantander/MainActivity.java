package com.example.sensorsantander;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Utilities.AdapterItem;
import Utilities.ClaseListas;
import Utilities.HttpHandler;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> sensorAmbList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*sensorAmbList = new ArrayList<>();
        lv = findViewById(R.id.list);

        */

        ArrayList<ClaseListas> adaptadorLista = new ArrayList<ClaseListas>();
        ListView lv = (ListView) findViewById(R.id.list);
        AdapterItem adapter = new AdapterItem(this, adaptadorLista);
        lv.setAdapter(adapter);

        new GetSensoresAmbientales().execute();
    }

    private class GetSensoresAmbientales extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://datos.santander.es/api/rest/datasets/sensores_smart_env_monitoring.json";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray sensores = jsonObj.getJSONArray("resources");

                    // looping through All sensors
                    for (int i = 0; i < sensores.length(); i++) {
                        JSONObject s = sensores.getJSONObject(i);
                        //String id = s.getString("cd:identifier");
                        String tipo = s.getString("ayto:type");
                        String ruido = s.getString("ayto:noise");
                        String luminosidad = s.getString("ayto:light");
                        String temperatura = s.getString("ayto:temperature");
                        String bateria = s.getString("ayto:battery");
                        String latitud = s.getString("ayto:latitude");
                        String longitud = s.getString("ayto:longitude");
                        String ultMod = s.getString("dc:modified");
                        String uri = s.getString("uri");

                        // tmp hash map for single sensor
                        HashMap<String, String> sensor = new HashMap<>();

                        // adding each child node to HashMap key => value
                        //sensor.put("id", id);
                        sensor.put("tipo", tipo);
                        sensor.put("ruido", ruido);
                        sensor.put("luminosidad", luminosidad);
                        sensor.put("temperatura", temperatura);
                        sensor.put("bateria", bateria);
                        sensor.put("latitud", latitud);
                        sensor.put("longitud", longitud);
                        sensor.put("ultModificacion", ultMod);
                        sensor.put("uri", uri);

                        // adding sensor to sensor list
                        sensorAmbList.add(sensor);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, sensorAmbList,
                    R.layout.content_main, new String[]{ "identificador","temperatura"},
                    new int[]{R.id.identificador, R.id.temperatura});
            lv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
