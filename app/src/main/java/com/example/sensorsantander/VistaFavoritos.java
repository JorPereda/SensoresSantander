package com.example.sensorsantander;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import datos.Alarma;
import datos.AlarmaRegistrada;
import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.PresenterVistaFavoritos;
import adapters.CustomExpandableListAdapter;
import services.AlarmasService;
import tasks.CompruebaAlarmaTask;
import tasks.GetSensorUnicoTask;
import tasks.LimpiezaAlarmasTask;
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
    private SensorAmbiental sensorSelected;
    private Parent grupoSelected;
    private long packedPosition;

    AlarmasService mService;
    MyServiceConnection mConn;
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

        //Task actualiza lista de favoritos
        new UpdateFavoritosTask(parents, this).execute();

        //Servicio de notificaciones de alarmas
        Intent intent = new Intent(this, AlarmasService.class);
        intent.putExtra("alarmas", listaAlarmas);
        startService(intent);


        /*expList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    Log.d("Posicion (grupo): ", String.valueOf(groupPosition));
                    Log.d("Posicion (child): ", String.valueOf(childPosition));

                    Toast.makeText(getActivityContext(), "Posicion de la lista: "+childPosition, Toast.LENGTH_LONG).show();

                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });*/

    }

    class MyServiceConnection implements ServiceConnection {
        private final String TAG = MyServiceConnection.class.getSimpleName();

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            mService = ((AlarmasService.LocalBinder) service).getService();
            mIsBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()");
            mService = null;
            mIsBound = false;
        }
    }

    @Override
    public ExpandableListView getExpList() {
        return expList;
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
    public boolean checkItemList(int indexChild, int indexGroup){

        //packedPosition = expList.getExpandableListPosition(position);
        //int index = expList.getFlatListPosition(packedPosition);

        //int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        //int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

        

        // if group item clicked //
        if (indexGroup == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            expList.setItemChecked(indexGroup, true);
            grupoSelected = parents.get(indexGroup);
            Log.d("Seleccion grupo: ", String.valueOf(indexGroup));
            Log.d("Seleccion grupo: ", grupoSelected.getNombre());
            actionModeEditar();
            return true;
        }

        if (indexChild == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

            // handle data
            expList.setItemChecked(indexChild, true);
            sensorSelected = parents.get(indexGroup).getChild(indexChild);
            Log.d("Seleccion child: ", String.valueOf(indexChild));
            Log.d("Seleccion child: ", sensorSelected.getTitulo());
            actionModeEditar();
            // return true as we are handling the event.
            return true;
        }
        return true;


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


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
            mode.setTitle("Choose your option");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
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
                            if (ExpandableListView.getPackedPositionType(packedPosition) ==
                                    ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                                Log.d("Posicion (grupo): ", String.valueOf(packedPosition));
                                parents.remove(grupoSelected);
                                VariablesGlobales.nombreGrupos.remove(grupoSelected.getNombre());
                            }
                            if (ExpandableListView.getPackedPositionType(packedPosition) ==
                                    ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                                Log.d("Posicion (child): ", String.valueOf(packedPosition));
                                for(Parent p : parents){
                                    p.removeChild(sensorSelected);
                                }
                            }
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
                case R.id.share:
                    if (ExpandableListView.getPackedPositionType(packedPosition) ==
                            ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                        Toast.makeText(getActivityContext(), "Selecciona un sensor para compartir", Toast.LENGTH_LONG).show();
                    }else{
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String shareBody = "";
                        if(sensorSelected.getTipo().equals("WeatherObserved")){
                            shareBody = sensorSelected.getTitulo() + "\n" + "Temp: " + sensorSelected.getTemperatura() +
                                        "\n" + "https://maps.google.com/?q=" + sensorSelected.getLatitud() + "," + sensorSelected.getLongitud();
                        }
                        if(sensorSelected.getTipo().equals("NoiseLevelObserved")){
                            shareBody = sensorSelected.getTitulo() + "\n" + "Noise: " + sensorSelected.getRuido() +
                                        "\n" + "https://maps.google.com/?q=" + sensorSelected.getLatitud() + "," + sensorSelected.getLongitud();
                        }
                        String shareSub = "Your subject";
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(myIntent, "Share using"));
                    }

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
                            if (ExpandableListView.getPackedPositionType(packedPosition) ==
                                    ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                                String nombreViejo = grupoSelected.getNombre();

                                int index = VariablesGlobales.nombreGrupos.indexOf(nombreViejo);
                                VariablesGlobales.nombreGrupos.set(index, nombreNuevo);

                                grupoSelected.setNombre(nombreNuevo);

                            }
                            if (ExpandableListView.getPackedPositionType(packedPosition) ==
                                    ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                                sensorSelected.setTitulo(nombreNuevo);
                            }
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