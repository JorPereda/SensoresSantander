package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ExecutionException;

import datos.SensorAmbiental;

import presenters.PresenterVistaMapa;
import utilities.Interfaces_MVP;

public class VistaSensorUnicoMapa extends AppCompatActivity implements Interfaces_MVP.RequiredViewMapaOps, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static Interfaces_MVP.ProvidedPresenterMapaOps mPresenter;

    SensorAmbiental sensor;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mapa_sensor_unico);

        mPresenter = new PresenterVistaMapa(this);

        Intent intent = getIntent();
        sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");

        mapView = findViewById(R.id.mapViewSensor);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        TextView t1 = findViewById(R.id.nombre_sensor_mapa);
        TextView t2 = findViewById(R.id.nombre_calle_sensor_mapa);

        t1.setText(sensor.getTitulo());
        t2.setText(sensor.getDireccion());

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String latitud;
        String longitud;
        String tipo;
        String nombre;
        LatLng marcador = null;

        latitud = sensor.getLatitud();
        longitud = sensor.getLongitud();
        tipo = sensor.getTipo();
        nombre = sensor.getTitulo();

        marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

        if (tipo.equals("WeatherObserved")) {
            googleMap.addMarker(new MarkerOptions().position(marcador).title(nombre).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        if (tipo.equals("NoiseLevelObserved")) {
            googleMap.addMarker(new MarkerOptions().position(marcador).title(nombre).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }


        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcador)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public Context getActivityContext() {
        return getApplicationContext();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
