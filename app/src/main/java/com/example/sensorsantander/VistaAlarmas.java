package com.example.sensorsantander;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import datos.Alarma;
import datos.SensorAmbiental;
import utilities.HttpHandler;
import utilities.ListAlarmasAdapter;
import utilities.TinyDB;

public class VistaAlarmas extends AppCompatActivity {

    private ListView listaAlarmasListView;
    private ListAlarmasAdapter mAdapter;
    private ArrayList<Alarma> listaAlarmas;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_alarmas);

        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");

        listaAlarmasListView = findViewById(R.id.lista_alarmas);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        listaAlarmasListView.setEmptyView(emptyText);

        mAdapter = new ListAlarmasAdapter(this, listaAlarmas);
        listaAlarmasListView.setAdapter(mAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();

        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);
    }


    @Override
    protected void onStart() {
        super.onStart();
        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");
    }

}