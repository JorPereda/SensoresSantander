package presenters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;
import com.example.sensorsantander.VistaMapa;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import datos.SensorAmbiental;
import datos.VariablesGlobales;
import services.SensorDataService;
import utilities.CustomExpandableListAdapter;
import utilities.Interfaces_MVP;
import utilities.TinyDB;


public class SensorAppPresenter implements Interfaces_MVP.ProvidedPresenterOps, Interfaces_MVP.RequiredPresenterOps {

    // View reference.
    private Interfaces_MVP.RequiredViewOps mView;

    // Model reference (o service)
    private Interfaces_MVP.ProvidedModelOps svc;

    private ArrayList<SensorAmbiental> sensorAmbList;
    private ArrayList<CustomExpandableListAdapter.Parent> parents;

    public SensorAppPresenter(){
        sensorAmbList = new ArrayList<>();
    }

    public SensorAppPresenter(Interfaces_MVP.RequiredViewOps view){
        mView = view;
        sensorAmbList = new ArrayList<>();
        parents = new ArrayList<>();
        //TinyDB tinydb = new TinyDB(mView.getAppContext());
        //parents = tinydb.getListParent("parents");
    }

    public SensorAppPresenter(Interfaces_MVP.ProvidedModelOps svc){
        this.svc = svc;
        sensorAmbList = new ArrayList<>();
        parents = new ArrayList<>();
        //parents = VariablesGlobales.parents;
    }

    /**
     * @return  Application context
     */
    @Override
    public Context getAppContext() {
        try {
            return getView().getAppContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * @return  Activity context
     */
    @Override
    public Context getActivityContext() {
        try {
            return getView().getActivityContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ArrayList<SensorAmbiental> getSensorAmbList() {
        return sensorAmbList;
    }

    public void setSensorAmbList(ArrayList<SensorAmbiental> sensorAmbList) {
        this.sensorAmbList = sensorAmbList;
    }

    @Override
    public ArrayList<SensorAmbiental> showSensorData(){
        getSensorData();
        return sensorAmbList;
    }

    @Override
    public void getSensorData() {
        sensorAmbList = new SensorDataService().getSensorData();
    }



    @Override
    public void showServerNotAvailable() {

    }

    @Override
    public void showConnectionNotAvailable() {

    }

    @Override
    public boolean menuFavoritos(MenuItem item, Activity activity){

        switch (item.getItemId()) {
            case R.id.irMapa:

                Intent abrirMapa = new Intent(mView.getActivityContext(), VistaMapa.class);
                mView.getActivityContext().startActivity(abrirMapa);
                return true;

            case R.id.action_add_element:

                final AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
                builder.setTitle("Introduce el nombre del nuevo grupo:");

                // Set up the input
                final EditText inputGrupo = new EditText(mView.getActivityContext());
                // Specify the type of input expected; this
                inputGrupo.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputGrupo);

                // Set up the buttons
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = inputGrupo.getText().toString();
                        CustomExpandableListAdapter.Parent grupo = new CustomExpandableListAdapter.Parent(m_Text);
                        parents.add(grupo);
                        mView.addToGroup(grupo);
                        VariablesGlobales.nombreGrupos.add(m_Text);
                        TinyDB tinydb = new TinyDB(mView.getAppContext());
                        tinydb.putListString("nombreGrupos", VariablesGlobales.nombreGrupos);

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

            case R.id.editMode:
                ImageButton botongrupo =  activity.findViewById(R.id.eliminargrupo_button);
                ImageButton botonsensor =  activity.findViewById(R.id.boton_eliminar_sensor);

                if(botongrupo.getVisibility()==View.INVISIBLE){
                    botongrupo.setVisibility(View.VISIBLE);
                } else if(botongrupo.getVisibility()==View.VISIBLE){
                    botongrupo.setVisibility(View.INVISIBLE);
                }

                if(botonsensor.getVisibility()==View.INVISIBLE){
                    botonsensor.setVisibility(View.VISIBLE);
                } else if(botonsensor.getVisibility()==View.VISIBLE){
                    botonsensor.setVisibility(View.INVISIBLE);
                }

                return true;

            default:

                return false;
        }

    }

    @Override
    public boolean menuMapa(MenuItem item, GoogleMap map) {
        TipoMapa tipo = new TipoMapa();

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(mView.getActivityContext(), "Refresh selected", Toast.LENGTH_SHORT).show();
                tipo.mapaCompleto(map);
                try {
                    new VistaMapa.DatosAsyncTask().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_home:
                Intent goFavs = new Intent(mView.getActivityContext(), VistaFavoritos.class);
                mView.getActivityContext().startActivity(goFavs);
                return true;
            case R.id.todos:
                //todos los sensores
                tipo.mapaCompleto(map);
                return true;
            case R.id.weather:
                //sensores atmosfericos
                tipo.mapaWeather(map);
                return true;
            case R.id.noise:
                //sensores de ruido
                tipo.mapaRuido(map);
                return true;
            default:
                return menuMapa(item, map);
        }
    }


    public class TipoMapa{

        public void mapaCompleto(GoogleMap googleMap){
            String latitud;
            String longitud;
            String tipo;
            String id;
            LatLng marcador = null;

            for(SensorAmbiental s: sensorAmbList) {
                latitud = s.getLatitud();
                longitud = s.getLongitud();
                tipo = s.getTipo();
                id = s.getIdentificador();

                marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

                if(tipo.equals("WeatherObserved")){
                    googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                if(tipo.equals("NoiseLevelObserved")){
                    googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        }

        public void mapaWeather(GoogleMap googleMap){
            googleMap.clear();
            String latitud;
            String longitud;
            String tipo;
            String id;
            LatLng marcador = null;

            for(SensorAmbiental s: sensorAmbList) {
                latitud = s.getLatitud();
                longitud = s.getLongitud();
                tipo = s.getTipo();
                id = s.getIdentificador();

                marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

                if(tipo.equals("WeatherObserved")){
                    googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }
        }

        public void mapaRuido(GoogleMap googleMap){
            googleMap.clear();
            String latitud;
            String longitud;
            String tipo;
            String id;
            LatLng marcador = null;

            for(SensorAmbiental s: sensorAmbList) {
                latitud = s.getLatitud();
                longitud = s.getLongitud();
                tipo = s.getTipo();
                id = s.getIdentificador();

                marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

                if(tipo.equals("NoiseLevelObserved")){
                    googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        }
    }

    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.RequiredViewOps getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }

    @Override
    public void onClickAddFavorito(final SensorAmbiental sensor, final String grupo){

        SharedPreferences prefs = mView.getActivityContext().getSharedPreferences("titulo sensor", Context.MODE_PRIVATE);

        TinyDB tinydb = new TinyDB(mView.getAppContext());
        parents = tinydb.getListParent("parents");

        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
        builder.setTitle("Introduce el nombre del nuevo sensor:");

        // Set up the input
        final EditText inputSensor = new EditText(mView.getActivityContext());
        // Specify the type of input expected; this
        inputSensor.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputSensor);

        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = inputSensor.getText().toString();
                CustomExpandableListAdapter.Child child = new CustomExpandableListAdapter.Child();
                child.setTitulo(m_Text);

                for (CustomExpandableListAdapter.Parent p : parents){
                    if(p.getNombre().equals(grupo)){
                        if(sensor.getTipo().equals("WeatherObserved")){
                            child.setTipo(sensor.getTipo());
                            child.setMedidaLabel("Temp: ");
                            child.setMedida(sensor.getTemperatura());
                        }
                        if(sensor.getTipo().equals("NoiseLevelObserved")){
                            child.setTipo(sensor.getTipo());
                            child.setMedidaLabel("Noise: ");
                            child.setMedida(sensor.getRuido());
                        }
                        p.addChild(child);
                    }
                    //parents = tinydb.getListParent("parents");
                    //parents.add(p);
                    TinyDB tinydb = new TinyDB(mView.getAppContext());
                    tinydb.putListParent("parents", parents);
                }

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

}
