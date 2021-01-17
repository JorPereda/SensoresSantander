package com.example.sensorsantander;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import datos.Alarma;
import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.PresenterVistaFavoritos;
import adapters.CustomExpandableListAdapter;
import services.AlarmsNotifService;
import tasks.UpdateFavoritosTask;
import utilities.Interfaces_MVP;
import utilities.TinyDB;


public class VistaFavoritos extends AppCompatActivity implements Interfaces_MVP.ViewFavoritosYAlarma {

    private static Interfaces_MVP.PresenterFavoritos mPresenter;

    private ArrayList<Parent> parents = new ArrayList<>();
    private ArrayList<Alarma> listaAlarmas = new ArrayList();
    private ArrayList<SensorAmbiental> sensorAmbList;

    private CustomExpandableListAdapter mAdapter;
    private ExpandableListView expList;

    private ActionMode mActionMode;
    private int groupPosition;
    private long packedPosition;

    //AlarmasService mService;
    //MyServiceConnection mConn;
    boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_favoritos);

        mPresenter = new PresenterVistaFavoritos(this);
        mPresenter.getListaSensores();

        TinyDB tinydb = new TinyDB(this);
        parents = tinydb.getListParent("parents");
        listaAlarmas = tinydb.getListAlarmas("alarmas");

        expList = findViewById(R.id.list_view_favoritos);
        expList.setSelector(R.drawable.selector_list_item);
        mAdapter = new CustomExpandableListAdapter(parents, this);
        expList.setAdapter(mAdapter);

        refreshScreen();
        onStartService();

    }

    @Override
    public void onStartService() {
        Intent i = new Intent(getBaseContext(), AlarmsNotifService.class);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra("alarmas", listaAlarmas);
        if(!(isMyServiceRunning(AlarmsNotifService.class))){
            startService(i);
        }
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Alarma> listaAlarmas;
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_OK);
            if (resultCode == RESULT_OK) {
                listaAlarmas = (ArrayList<Alarma>) intent.getSerializableExtra("alarmasResult");
                //Toast.makeText(VistaFavoritos.this, String.valueOf(listaAlarmas.size()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(VistaFavoritos.this, String.valueOf(listaAlarmas.get(1).getAlarmasRegistradas().size()), Toast.LENGTH_SHORT).show();
                updateListAlarmas(listaAlarmas);
            }
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExpandableListView getExpList() {
        return expList;
    }

    @Override
    public void refreshScreen(){
        //Task actualiza lista de favoritos
        new UpdateFavoritosTask(parents, this).execute();
        //updateListView(parents);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateParentInList(Parent parent) {
        this.parents.set(parents.indexOf(parent), parent);
    }

    @Override
    public void updateListParents(ArrayList<Parent> parents) {
        this.parents = parents;
    }

    @Override
    public void updateListAlarmas(ArrayList<Alarma> alarmas) {
        this.listaAlarmas = alarmas;
        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);
    }

    @Override
    public void updateAlarmInList(Alarma alarma) {
        this.listaAlarmas.set(listaAlarmas.indexOf(alarma), alarma);
        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);
    }

    @Override
    public void updateListTotal(ArrayList<SensorAmbiental> sensorAmbList) {
        this.sensorAmbList = sensorAmbList;
    }

    @Override
    public void updateListView(ArrayList<Parent> parents){
        this.parents = parents;
        expList.invalidateViews();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void actionModeEditar(int groupPosition){
        this.groupPosition = groupPosition;
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

        IntentFilter filter = new IntentFilter(AlarmsNotifService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
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
    public Context getActivityContext() {
        return this;
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void addToGroup(Parent grupo) {
        parents.add(grupo);
        mAdapter.setData(parents);
    }

    @Override
    public PresenterVistaFavoritos getPresenter() {
        return (PresenterVistaFavoritos) mPresenter;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){

        Parent grupoAction;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
            mode.setTitle("Choose your option");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            grupoAction = parents.get(groupPosition);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.delete:
                    final AlertDialog.Builder builderDelete = new AlertDialog.Builder(getActivityContext());
                    builderDelete.setTitle("¿Realmente deseas eliminar?");

                    // Set up the buttons
                    builderDelete.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parents.remove(grupoAction);
                            VariablesGlobales.nombreGrupos.remove(grupoAction.getNombre());
                            mAdapter.setData(parents);
                        }
                    });
                    builderDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builderDelete.show();

                    mode.finish();
                    return true;
                case R.id.rename:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                    builder.setTitle("Introduce el nuevo nombre:");

                    // Set up the input
                    final EditText inputRename = new EditText(getActivityContext());
                    // Specify the type of input expected; this
                    inputRename.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(inputRename);

                    // Set up the buttons
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nombreNuevo = inputRename.getText().toString();
                            String nombreViejo = grupoAction.getNombre();

                            int index = VariablesGlobales.nombreGrupos.indexOf(nombreViejo);
                            VariablesGlobales.nombreGrupos.set(index, nombreNuevo);
                            grupoAction.setNombre(nombreNuevo);

                            mAdapter.setData(parents);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    return true;
                default:
                    return false;
            }        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

}