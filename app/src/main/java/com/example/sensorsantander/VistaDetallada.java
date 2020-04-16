package com.example.sensorsantander;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.HashMap;

public class VistaDetallada extends AppCompatActivity {

    HashMap<String, String> sensor;

    String id;
    String tipo;
    String ruido;
    String luminosidad;
    String temperatura;
    String bateria;
    String latitud;
    String longitud;
    String ultMod;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_detalle);

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


    public void llenarCampos(){
        TextView idTV = findViewById(R.id.id_detalle_text);
        TextView tempTV = findViewById(R.id.temp_detalle_text);
        TextView noiseTV = findViewById(R.id.noise_detalle_text);
        TextView lightTV = findViewById(R.id.light_detalle_text);

        idTV.setText(id);
        tempTV.setText(temperatura);
        noiseTV.setText(ruido);
        lightTV.setText(luminosidad);
    }
}
