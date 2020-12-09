package com.example.sensorsantander;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;

import adapters.ListAlarmasRegistradasAdapter;
import datos.Alarma;
import datos.AlarmaRegistrada;
import datos.Parent;
import datos.SensorAmbiental;
import presenters.PresenterVistaFavoritos;
import tasks.CompruebaAlarmaTask;
import tasks.LimpiezaAlarmasTask;
import utilities.Interfaces_MVP;
import adapters.ListAlarmasAdapter;
import utilities.TinyDB;

public class VistaAlarmas extends AppCompatActivity implements Interfaces_MVP.ViewFavoritosYAlarma {

    private static final String CHANNEL_ID = "1";
    private ListView listaAlarmasListView;
    private ListAlarmasAdapter mAdapter;
    private ArrayList<Alarma> listaAlarmas = new ArrayList<>();

    private static Interfaces_MVP.PresenterFavoritos mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_alarmas);

        Intent intent = getIntent();
        String nombreAlarmaNotificacion =  intent.getStringExtra("nombre");

        mPresenter = new PresenterVistaFavoritos(this);

        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");

        listaAlarmasListView = findViewById(R.id.lista_alarmas);
        TextView emptyText = findViewById(android.R.id.empty);
        listaAlarmasListView.setEmptyView(emptyText);

        mAdapter = new ListAlarmasAdapter(listaAlarmas, nombreAlarmaNotificacion, this);
        listaAlarmasListView.setAdapter(mAdapter);

        for(Alarma alarma : listaAlarmas){
            new LimpiezaAlarmasTask(this, alarma).execute();
        }


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

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public Context getActivityContext() {
        return getApplicationContext();
    }

    @Override
    public void addToGroup(Parent grupo) {

    }

    @Override
    public PresenterVistaFavoritos getPresenter() {
        return (PresenterVistaFavoritos) mPresenter;
    }

    @Override
    public boolean checkItemList(int indexChild, int indexGroup) {
        return false;
    }

    @Override
    public void actionModeEditar() {

    }

    @Override
    public ExpandableListView getExpList() {
        return null;
    }

    @Override
    public void updateParentInList(Parent parent) {

    }

    @Override
    public void updateListParents(ArrayList<Parent> parents) {

    }

    @Override
    public void updateListAlarmas(ArrayList<Alarma> alarmas) {
        this.listaAlarmas = alarmas;
    }

    @Override
    public void updateAlarmInList(Alarma alarma) {
        this.listaAlarmas.set(listaAlarmas.indexOf(alarma), alarma);
        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);


    }

    @Override
    public void updateListTotal(ArrayList<SensorAmbiental> sensorAmbList) {

    }

    @Override
    public void updateListView(ArrayList<Parent> parents) {
        mAdapter.notifyDataSetChanged();
    }
}