package presenters;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;
import com.example.sensorsantander.VistaMapa;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import datos.Parent;
import datos.SensorAmbiental;
import services.SensorDataService;
import utilities.Interfaces_MVP;

public class PresenterVistaMapa implements Interfaces_MVP.ProvidedPresenterMapaOps, Interfaces_MVP.RequiredPresenterOps {

    // View reference.
    private Interfaces_MVP.RequiredViewMapaOps mView;

    // Model reference (o service)
    private Interfaces_MVP.ProvidedModelOps svc;


    private ArrayList<SensorAmbiental> sensorAmbList;
    private ArrayList<Parent> parents;

    public PresenterVistaMapa(){
        sensorAmbList = new ArrayList<>();
    }

    public PresenterVistaMapa(Interfaces_MVP.RequiredViewMapaOps view){
        mView = view;
        sensorAmbList = new ArrayList<>();
        parents = new ArrayList<>();
        //TinyDB tinydb = new TinyDB(mView.getAppContext());
        //parents = tinydb.getListParent("parents");
    }

    public PresenterVistaMapa(Interfaces_MVP.ProvidedModelOps svc){
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
        sensorAmbList = new SensorDataService(mView.getActivityContext()).getSensorData();
    }



    @Override
    public void showServerNotAvailable() {

    }

    @Override
    public void showConnectionNotAvailable() {

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

        public void mapaIndividual(GoogleMap googleMap, SensorAmbiental sensor){

            LatLng marcador;

            marcador = new LatLng(Double.valueOf(sensor.getLatitud()), Double.valueOf(sensor.getLongitud()));

            googleMap.addMarker(new MarkerOptions().position(marcador).title(sensor.getTitulo()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        }
    }

    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.RequiredViewMapaOps getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }

}
