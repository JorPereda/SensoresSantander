package com.example.sensorsantander;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;



import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.SensorAppPresenter;
import utilities.ComplexPreferences;
import utilities.CustomExpandableListAdapter;
import utilities.Interfaces_MVP;
import utilities.ListComplexFavoritos;
import utilities.ListComplexGrupos;


public class VistaFavoritos extends AppCompatActivity implements View.OnClickListener, Interfaces_MVP.RequiredViewOps {

    private static Interfaces_MVP.ProvidedPresenterOps mPresenter;

    private ArrayList<CustomExpandableListAdapter.Parent> parents = new ArrayList<>();
    private ArrayList<SensorAmbiental> listaFavoritos = new ArrayList<>();
    private ArrayList<String> listaGrupos = new ArrayList<>();

    private ComplexPreferences complexPreferencesFav, complexPreferencesGroup;
    private ListComplexFavoritos complexObjectFav = new ListComplexFavoritos();
    private ListComplexGrupos complexObjectGroup = new ListComplexGrupos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_favoritos);

        mPresenter = new SensorAppPresenter(this);


        //Codigo que solo se ejecutará al instalarse y no al ejecutarse cada vez
        //SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        //if (isFirstRun)
        //{
            // Code to run once
            complexObjectFav.setLista(listaFavoritos);

            ComplexPreferences  complexPreferencesFav = ComplexPreferences.getComplexPreferences(getBaseContext(), "myfav", MODE_PRIVATE);
            complexPreferencesFav.putObject("list", complexObjectFav);
            complexPreferencesFav.commit();


            complexObjectGroup.setLista(listaGrupos);

            ComplexPreferences  complexPreferencesGroup = ComplexPreferences.getComplexPreferences(getBaseContext(), "mygroups", MODE_PRIVATE);
            complexPreferencesGroup.putObject("grupos", complexObjectGroup);
            complexPreferencesGroup.commit();

            //SharedPreferences.Editor editor = wmbPreference.edit();
            //editor.putBoolean("FIRSTRUN", false);
            //editor.commit();
        //}
        ////////////////////////////////////////////

        complexPreferencesFav = ComplexPreferences.getComplexPreferences(this, "myfav", MODE_PRIVATE);
        complexObjectFav = complexPreferencesFav.getObject("list", ListComplexFavoritos.class);
        listaFavoritos = complexObjectFav.getLista();

        complexPreferencesGroup = ComplexPreferences.getComplexPreferences(this, "mygroups", MODE_PRIVATE);
        complexObjectGroup = complexPreferencesGroup.getObject("grupos", ListComplexGrupos.class);
        listaGrupos = complexObjectGroup.getLista();
        VariablesGlobales.nombreGrupos = listaGrupos;

        ExpandableListView exList = findViewById(R.id.list_view_favoritos);

        //Create ExpandableListAdapter Object
        final CustomExpandableListAdapter mAdapter = new CustomExpandableListAdapter(this, parents);

        //Rellenar lista
        CustomExpandableListAdapter.Parent parentDefault = new CustomExpandableListAdapter.Parent("default");

        CustomExpandableListAdapter.Parent p = new CustomExpandableListAdapter.Parent("name");

        for(SensorAmbiental sensor : listaFavoritos){
            ArrayList<CustomExpandableListAdapter.Child> children = new ArrayList<CustomExpandableListAdapter.Child>();
            CustomExpandableListAdapter.Child child = new CustomExpandableListAdapter.Child();
            child.setTitulo(sensor.getIdentificador());
            child.setTipo(sensor.getTipo());
            if(sensor.getTipo().equals("WeatherObserved")){
                child.setMedidaLabel("Temp: ");
                child.setMedida(sensor.getTemperatura());
            }
            if(sensor.getTipo().equals("NoiseLevelObserved")){
                child.setMedidaLabel("Noise: ");
                child.setMedida(sensor.getRuido());
            }

            if(child.getGrupo().equals("default")){
                children.add(child);
                parentDefault.setChildren(children);
                parents.add(parentDefault);
            }else{
                p.setNombre(child.getGrupo());
                children.add(child);
                p.setChildren(children);
                parents.add(p);
            }

        }

        exList.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoritos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuFavoritos(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    public ArrayList<CustomExpandableListAdapter.Parent> getParents() {
        return parents;
    }

    public void setParents(ArrayList<CustomExpandableListAdapter.Parent> parents) {
        this.parents = parents;
    }

    @Override
    public void addToGroup(CustomExpandableListAdapter.Parent grupo) {
        this.parents.add(grupo);
    }

}