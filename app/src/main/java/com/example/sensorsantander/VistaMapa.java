package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


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

import datos.SensorAmbiental;
import presenters.PresenterVistaMapa;
import utilities.CustomMarkerInfoWindowView;
import utilities.Interfaces_MVP;

public class VistaMapa extends AppCompatActivity  implements Interfaces_MVP.ViewMapa, GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    private static Interfaces_MVP.PresenterMapa mPresenter;

    static ArrayList<SensorAmbiental> sensorAmbList = new ArrayList<>();
    static ArrayList<SensorAmbiental> listaFavoritos = new ArrayList<>();

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mapa);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        sensorAmbList = (ArrayList<SensorAmbiental>) intent.getSerializableExtra("listaSensores");
        mPresenter = new PresenterVistaMapa(this, sensorAmbList);

/*
        //Necesario el ".get" para que la aplicacion espere a tener los datos cargados y pueda
        //crear los marcadores para el mapa.

        try {
            new DatosAsyncTask().execute().get();
            VariablesGlobales.listaSensoresCompleta = sensorAmbList;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }

    public static ArrayList<SensorAmbiental> getListaFavoritos() {
        return listaFavoritos;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowView(this));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String identificador;
                SensorAmbiental sensor = new SensorAmbiental();
                Intent intent = new Intent(VistaMapa.this, VistaDetallada.class);
                for (SensorAmbiental s : sensorAmbList) {
                    identificador = s.getTipo() + s.getIdentificador();
                    if(identificador.equals(marker.getTitle())){
                        sensor = s;
                    }
                }
                intent.putExtra("sensor", sensor);
                startActivity(intent);
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

        for (SensorAmbiental s : sensorAmbList) {
            latitud = s.getLatitud();
            longitud = s.getLongitud();
            tipo = s.getTipo();
            id = s.getIdentificador();

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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuMapa(item, map);
    }


}
