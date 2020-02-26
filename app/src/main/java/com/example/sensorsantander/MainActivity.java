package com.example.sensorsantander;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Datos.LatLngBean;
import Utilities.HttpHandler;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback{

    private String TAG = MainActivity.class.getSimpleName();


    HashMap<Marker,LatLngBean> hashMapMarker = new HashMap<Marker,LatLngBean>();

    ArrayList<HashMap<String, String>> sensorAmbList;
    private ListView lv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorAmbList = new ArrayList<>();
        lv = findViewById(R.id.list);
        new GetSensoresAmbientales().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detallesSensor = new Intent(view.getContext(), VistaDetallada.class);
                HashMap<String, String> sensor = sensorAmbList.get(position);
                detallesSensor.putExtra("map",sensor);
                startActivity(detallesSensor);
            }
        });

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
                        String id = s.getString("dc:identifier");
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
                        sensor.put("id", id);
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
                    R.layout.lista_personalizada, new String[]{"id","temperatura"},
                    new int[]{R.id.identificador, R.id.temperatura});
            lv.setAdapter(adapter);

         }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        String latitud;
        String longitud;
        List<LatLng> marcadores = new ArrayList<>();
        MarkerOptions options = new MarkerOptions();
        LatLng marcadorInicial = new LatLng(43.46, -3.81);
        marcadores.add(marcadorInicial);
        for(HashMap<String, String> sensor : sensorAmbList) {

            latitud = sensor.get("latitud");
            longitud = sensor.get("longitud");

            LatLng marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

            marcadores.add(marcador);
        }

        for (LatLng point : marcadores) {
            options.position(point);
            options.title("someTitle");
            options.snippet("someDesc");
            googleMap.addMarker(options);
        }

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcadorInicial)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
