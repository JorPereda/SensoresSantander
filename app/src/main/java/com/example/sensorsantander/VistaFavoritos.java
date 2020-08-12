package com.example.sensorsantander;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.SensorAppPresenter;
import utilities.CustomExpandableListAdapter;
import utilities.Interfaces_MVP;
import utilities.TinyDB;

import static android.content.ContentValues.TAG;


public class VistaFavoritos extends AppCompatActivity implements View.OnClickListener, Interfaces_MVP.RequiredViewOps {

    private static Interfaces_MVP.ProvidedPresenterOps mPresenter;

    private ArrayList<CustomExpandableListAdapter.Parent> parents = new ArrayList<>();

    private CustomExpandableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_favoritos);

        mPresenter = new SensorAppPresenter(this);

        TinyDB tinydb = new TinyDB(this);
        parents = tinydb.getListParent("parents");

        ExpandableListView exList = findViewById(R.id.list_view_favoritos);
        mAdapter = new CustomExpandableListAdapter(this, parents);
        exList.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        TinyDB tinydb = new TinyDB(this);

        tinydb.putListString("nombreGrupos", VariablesGlobales.nombreGrupos);
        tinydb.putListParent("parents", parents);

    }


    @Override
    protected void onStart() {
        super.onStart();
        TinyDB tinydb = new TinyDB(this);
        VariablesGlobales.nombreGrupos = tinydb.getListString("nombreGrupos");
        parents = tinydb.getListParent("parents");

    }

    @Override
    protected void onResume() {
        super.onResume();
        TinyDB tinydb = new TinyDB(this);
        VariablesGlobales.nombreGrupos = tinydb.getListString("nombreGrupos");
        parents = tinydb.getListParent("parents");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoritos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuFavoritos(item, this);
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

    @Override
    public void addToGroup(CustomExpandableListAdapter.Parent grupo) {
        parents.add(grupo);
    }

    @Override
    public void reloadAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}