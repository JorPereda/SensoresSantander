package com.example.sensorsantander;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Utilities.CustomMarkerInfoWindowView;
import Utilities.HttpHandler;
import Utilities.MyReceiver;

public class MainActivity extends AppCompatActivity  implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    private String TAG = MainActivity.class.getSimpleName();

    ArrayList<HashMap<String, String>> sensorAmbList;

    private BroadcastReceiver myReceiver = null;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorAmbList = new ArrayList<>();

        myReceiver = new MyReceiver();
        broadcastIntent();
        //Necesario el ".get" para que la aplicacion espere a tener los datos cargados y pueda
        //crear los marcadores para el mapa.
        try {
            new GetSensoresAmbientales().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void broadcastIntent() {
        registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
            /*ListAdapter adapter = new SimpleAdapter(MainActivity.this, sensorAmbList,
                    R.layout.lista_personalizada, new String[]{"id","temperatura"},
                    new int[]{R.id.identificador, R.id.temperatura});
            lv.setAdapter(adapter);*/



         }
    }

    public void mapaTotal(GoogleMap googleMap){
        String latitud, longitud, tipo, id;
        LatLng marcador = null;

        for(HashMap<String, String> map : sensorAmbList) {
            latitud = map.get("latitud");
            longitud = map.get("longitud");
            tipo = map.get("tipo");
            id = map.get("id");

            marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));


            if(tipo.equals("WeatherObserved")){
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            if(tipo.equals("NoiseLevelObserved")){
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

        }

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcador)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



    }

    public void mapaWeather(GoogleMap googleMap){
        googleMap.clear();
        String latitud, longitud, tipo, id;
        LatLng marcador = null;

        for(HashMap<String, String> map : sensorAmbList) {
            latitud = map.get("latitud");
            longitud = map.get("longitud");
            tipo = map.get("tipo");
            id = map.get("id");

            marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

            if(tipo.equals("WeatherObserved")){
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

        }

    }

    public void mapaRuido(GoogleMap googleMap){
        googleMap.clear();
        String latitud, longitud, tipo, id;
        LatLng marcador = null;

        for(HashMap<String, String> map : sensorAmbList) {
            latitud = map.get("latitud");
            longitud = map.get("longitud");
            tipo = map.get("tipo");
            id = map.get("id");

            marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

            if(tipo.equals("NoiseLevelObserved")){
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapaTotal(googleMap);
        //googleMap.setOnMarkerClickListener(MainActivity.this);
        //CustomMarkerInfoWindowView markerWindowView = new CustomMarkerInfoWindowView();
        //googleMap.setInfoWindowAdapter(markerWindowView);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("title",""+ marker.getTitle());
        Log.d("position",""+ marker.getPosition());
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.todos:
                //todos los sensores
                mapaTotal(map);
                return true;
            case R.id.weather:
                //sensores atmosfericos
                mapaWeather(map);
                return true;
            case R.id.noise:
                //sensores de ruido
                mapaRuido(map);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
