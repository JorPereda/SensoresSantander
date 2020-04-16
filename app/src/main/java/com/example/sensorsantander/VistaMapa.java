package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import presenters.SensorAppPresenter;
import utilities.CustomMarkerInfoWindowView;
import utilities.Interfaces_MVP;

public class VistaMapa extends AppCompatActivity  implements Interfaces_MVP.RequiredViewOps, GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    private static Interfaces_MVP.ProvidedPresenterOps mPresenter;

    static ArrayList<HashMap<String, String>> sensorAmbList;
    static ArrayList<HashMap<String, String>> listaFavoritos;

    public static ArrayList<HashMap<String, String>> getListaFavoritos() {
        return listaFavoritos;
    }

    private GoogleMap map;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sensorAmbList = new ArrayList<>();
        listaFavoritos = new ArrayList<>();
        mPresenter = new SensorAppPresenter(this);



        //Necesario el ".get" para que la aplicacion espere a tener los datos cargados y pueda
        //crear los marcadores para el mapa.

        try {
            new DatosAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static class DatosAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.bringToFront();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            sensorAmbList = mPresenter.showSensorData();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowView(this));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String identificador;
                HashMap<String, String> sensor = new HashMap<>();
                Intent intent = new Intent(VistaMapa.this,VistaDetallada.class);
                for (HashMap<String, String> hashmap : sensorAmbList) {
                    identificador = hashmap.get("tipo") + hashmap.get("id");
                    if(identificador.equals(marker.getTitle())){
                        sensor = hashmap;
                    }
                }
                intent.putExtra("map",sensor);
                startActivity(intent);
            }
        });

        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                String identificador;
                for (HashMap<String, String> hashmap : sensorAmbList) {
                    identificador = hashmap.get("tipo") + hashmap.get("id");
                    if(identificador.equals(marker.getTitle())){
                        listaFavoritos.add(hashmap);
                        Toast.makeText(VistaMapa.this ,"Sensor "+ marker.getTitle() + " añadido a favoritos",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(VistaMapa.this ,"No se ha añadido",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mapaTotal(googleMap);
    }

    public void mapaTotal(GoogleMap googleMap) {
        String latitud;
        String longitud;
        String tipo;
        String id;
        LatLng marcador = null;

        for (HashMap<String, String> hashmap : sensorAmbList) {
            latitud = hashmap.get("latitud");
            longitud = hashmap.get("longitud");
            tipo = hashmap.get("tipo");
            id = hashmap.get("id");

            marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

            if (tipo.equals("WeatherObserved")) {
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            if (tipo.equals("NoiseLevelObserved")) {
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

        }

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcador)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Toast.makeText(this ,"Descargando datos",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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
        return mPresenter.menuOptionsClicked(item, map);
    }


}
