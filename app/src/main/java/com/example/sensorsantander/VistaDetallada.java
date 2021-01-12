package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import datos.Alarma;
import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.PresenterVistaFavoritos;
import utilities.Interfaces_MVP;

public class VistaDetallada extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Interfaces_MVP.ViewFavoritosYAlarma {

    private static Interfaces_MVP.PresenterFavoritos mPresenter;

    private SensorAmbiental sensor;

    private String id;
    private String tipo;
    private String ruido;
    private String luminosidad;
    private String temperatura;
    private String bateria;
    private String latitud;
    private String longitud;
    private String ultMod;
    private String uri;

    private Spinner selectorGrupo;
    private String grupoSeleccionado;
    private Button botonAddFav;

    private ArrayList<String> grupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_detalle);
        mPresenter = new PresenterVistaFavoritos(this);

        //TinyDB tinydb = new TinyDB(this);
        //grupos = tinydb.getListString("nombreGrupos");
        grupos = VariablesGlobales.nombreGrupos;

        Intent intent = getIntent();
        sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");

        id = sensor.getIdentificador();
        tipo = sensor.getTipo();
        ruido = sensor.getRuido();
        luminosidad = sensor.getLuminosidad();
        temperatura = sensor.getTemperatura();
        bateria = sensor.getBattery();
        latitud = sensor.getLatitud();
        longitud = sensor.getLongitud();
        ultMod = sensor.getUltModificacion();
        uri = sensor.getUri();

        llenarCampos();

        selectorGrupo = findViewById(R.id.selector_grupo);
        selectorGrupo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grupos));
        selectorGrupo.setOnItemSelectedListener(this);

        botonAddFav = findViewById(R.id.boton_add_favoritos);
        botonAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickAddFavorito(sensor, grupoSeleccionado);
            }
        });

    }



    public void llenarCampos(){
        TextView idTV = findViewById(R.id.id_detalle_text);
        TextView tempTV = findViewById(R.id.temp_detalle_text);
        TextView noiseTV = findViewById(R.id.noise_detalle_text);
        TextView lightTV = findViewById(R.id.light_detalle_text);
        TextView ultModTV = findViewById(R.id.ult_modificacion_text);

        idTV.setText(id);
        tempTV.setText(temperatura);
        noiseTV.setText(ruido);
        lightTV.setText(luminosidad);
        String year = ultMod.substring(0,4);
        String mes = ultMod.substring(5,7);
        String dia = ultMod.substring(8,10);
        String hora = ultMod.substring(11,16);
        ultModTV.setText(dia+"/"+mes+"/"+year+" "+hora);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.selector_grupo:
                grupoSeleccionado = parent.getSelectedItem().toString();
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void addToGroup(Parent grupo) {

    }

    @Override
    public PresenterVistaFavoritos getPresenter() {
        return (PresenterVistaFavoritos) mPresenter;
    }

    @Override
    public void updateListTotal(ArrayList<SensorAmbiental> sensorAmbList) {

    }

    @Override
    public void updateListView(ArrayList<Parent> parents) {

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

    }

    @Override
    public void updateAlarmInList(Alarma alarma) {

    }
}
