package com.example.sensorsantander;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class VistaDetallada extends AppCompatActivity
        implements OnMapReadyCallback {

    HashMap<String, String> sensor;

    String id, tipo, ruido, luminosidad, temperatura, bateria, latitud, longitud, ultMod, uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_detalle);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        sensor = (HashMap<String, String>)intent.getSerializableExtra("map");

        id = sensor.get("id");
        tipo = sensor.get("tipo");
        ruido = sensor.get("ruido");
        luminosidad = sensor.get("luminosidad");
        temperatura = sensor.get("temperatura");
        bateria = sensor.get("bateria");
        latitud = sensor.get("latitud");
        longitud = sensor.get("longitud");
        ultMod = sensor.get("ultModificacion");
        uri = sensor.get("uri");

        llenarCampos();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));
        googleMap.addMarker(new MarkerOptions().position(marcador)
                .title("Sensor "+id));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcador)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void llenarCampos(){
        TextView idTV = (TextView)findViewById(R.id.id_detalle_text);
        TextView tempTV = (TextView)findViewById(R.id.temp_detalle_text);
        TextView noiseTV = (TextView)findViewById(R.id.noise_detalle_text);
        TextView lightTV = (TextView)findViewById(R.id.light_detalle_text);

        idTV.setText(id);
        tempTV.setText(temperatura);
        noiseTV.setText(ruido);
        lightTV.setText(luminosidad);
    }
}
