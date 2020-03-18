package com.example.sensorsantander;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Utilities.ServerResponse;

public class VistaMapa extends AppCompatActivity  implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    private String TAG = VistaMapa.class.getSimpleName();
    ArrayList<HashMap<String, String>> sensorAmbList;

    private GoogleMap map;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(10);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorAmbList = new ArrayList<>();

        //Necesario el ".get" para que la aplicacion espere a tener los datos cargados y pueda
        //crear los marcadores para el mapa.
        try {
            new GetSensoresAmbientales().execute().get();
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException: " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException: " + e.getMessage());
        }

    }

    private class GetSensoresAmbientales extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.bringToFront();
            Toast.makeText(VistaMapa.this,"Descargando datos",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sensorAmbList = new ServerResponse().getResponse();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
         }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
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
        //googleMap.setOnMarkerClickListener(VistaMapa.this);
        //CustomMarkerInfoWindowView markerWindowView = new CustomMarkerInfoWindowView();
        //googleMap.setInfoWindowAdapter(markerWindowView);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Log.d("title",""+ marker.getTitle());
        //Log.d("position",""+ marker.getPosition());
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
