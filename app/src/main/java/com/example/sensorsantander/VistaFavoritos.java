package com.example.sensorsantander;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


import utilities.CustomAdapterFavoritos;


public class VistaFavoritos extends Activity {

    ArrayList<HashMap<String, String>> listaFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_favoritos);
        listaFavoritos = new ArrayList<>();
        listaFavoritos = VistaMapa.getListaFavoritos();

        final ListView listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                String identificador;
                HashMap<String, String> sensor = new HashMap<>();

                Intent vistaDetalle = new Intent(VistaFavoritos.this, VistaDetallada.class);

                for (HashMap<String, String> hashmap : listaFavoritos) {
                    if(hashmap == adapter.getAdapter().getItem(position)){
                        sensor = hashmap;
                    }
                }
                vistaDetalle.putExtra("map",sensor);
                startActivity(vistaDetalle);
            }
        });

        listView.setAdapter(new CustomAdapterFavoritos(this,listaFavoritos));

    }



}