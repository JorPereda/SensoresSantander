package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import datos.Alarma;
import datos.AlarmaRegistrada;
import datos.Parent;
import datos.SensorAmbiental;
import presenters.PresenterVistaAlarmas;
import presenters.PresenterVistaFavoritos;
import utilities.Interfaces_MVP;
import adapters.ListAlarmasAdapter;
import utilities.TinyDB;

public class VistaAlarmas extends AppCompatActivity implements Interfaces_MVP.ViewFavoritosYAlarma {

    private static final String CHANNEL_ID = "1";
    private ListView listaAlarmasListView;
    private ListAlarmasAdapter mAdapter;
    private ArrayList<Alarma> listaAlarmas = new ArrayList<>();

    private static Interfaces_MVP.PresenterAlarma mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_alarmas);

        //Intent intent = getIntent();
        //String nombreAlarmaNotificacion = intent.getStringExtra("nombre");

        mPresenter = new PresenterVistaAlarmas(this);

        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");
        Log.e("Alarma vistaAlarmas", "Tamaño lista alarmas " + listaAlarmas.size());

        listaAlarmasListView = findViewById(R.id.lista_alarmas);
        TextView emptyText = findViewById(android.R.id.empty);
        listaAlarmasListView.setEmptyView(emptyText);

        mAdapter = new ListAlarmasAdapter(listaAlarmas, this);
        listaAlarmasListView.setAdapter(mAdapter);

        /*for(Alarma alarma : listaAlarmas){
            new LimpiezaAlarmasTask(this, alarma).execute();
        }*/


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
    public void actionModeEditar(int groupPosition) {

    }

    @Override
    public ExpandableListView getExpList() {
        return null;
    }

    @Override
    public void onStartService() {

    }

    @Override
    public void refreshScreen() {

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
    public void updateListAlarmasRegistradas(Alarma alarma, ArrayList<AlarmaRegistrada> alarmasRegistradas) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarmas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuAlarmas(item);
    }
}