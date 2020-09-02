package com.example.sensorsantander;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

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

    private ActionMode mActionMode;



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
    public void actionModeEditar(){
        mActionMode = startSupportActionMode(mActionModeCallback);
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
        mAdapter.setData(parents);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
            mode.setTitle("Choose your option");
            CheckBox cb = findViewById(R.id.checkbox_eliminar_sensor);
            cb.setVisibility(View.VISIBLE);
            ExpandableListView expList = findViewById(R.id.list_view_favoritos);
            SparseBooleanArray itemsChecked = expList.getCheckedItemPositions();

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_1:
                    Toast.makeText(VistaFavoritos.this, "Option 1 selected", Toast.LENGTH_SHORT).show();

                    mode.finish();
                    return true;
                case R.id.option_2:
                    Toast.makeText(VistaFavoritos.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            CheckBox cb = findViewById(R.id.checkbox_eliminar_sensor);
            cb.setVisibility(View.INVISIBLE);
            mActionMode = null;
        }
    };

}