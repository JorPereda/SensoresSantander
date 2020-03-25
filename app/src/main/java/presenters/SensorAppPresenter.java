package presenters;

import android.view.MenuItem;

import com.example.views.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import services.SensorDataService;
import utilities.Interfaces_MVP;

public class SensorAppPresenter implements Interfaces_MVP.ProvidedPresenterOps, Interfaces_MVP.RequiredPresenterOps {

    // View reference
    private Interfaces_MVP.RequiredViewOps view;
    // Model reference (o service)
    private Interfaces_MVP.ProvidedModelOps svc;

    ArrayList<HashMap<String, String>> sensorAmbList;

    public SensorAppPresenter(Interfaces_MVP.RequiredViewOps view){
        this.view = view;
        sensorAmbList = new ArrayList<>();
    }

    @Override
    public void getSensorData() {
        sensorAmbList = (ArrayList<HashMap<String, String>>) new SensorDataService(this).getSensorData();
    }

    @Override
    public ArrayList<HashMap<String, String>> showSensorData() {
        getSensorData();
        return sensorAmbList;
    }

    @Override
    public void showServerNotAvailable() {

    }

    @Override
    public void showConnectionNotAvailable() {

    }

    @Override
    public boolean menuOptionsClicked(MenuItem item, GoogleMap map) {
        TipoMapa tipo = new TipoMapa();

        switch (item.getItemId()) {
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
                return menuOptionsClicked(item, map);
        }
    }

    public class TipoMapa{

        public void mapaCompleto(GoogleMap googleMap){
            String latitud;
            String longitud;
            String tipo;
            String id;
            LatLng marcador = null;

            for(HashMap<String, String> hashmap : sensorAmbList) {
                latitud = hashmap.get("latitud");
                longitud = hashmap.get("longitud");
                tipo = hashmap.get("tipo");
                id = hashmap.get("id");

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

            for(HashMap<String, String> hashmap : sensorAmbList) {
                latitud = hashmap.get("latitud");
                longitud = hashmap.get("longitud");
                tipo = hashmap.get("tipo");
                id = hashmap.get("id");

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

            for(HashMap<String, String> hashmap : sensorAmbList) {
                latitud = hashmap.get("latitud");
                longitud = hashmap.get("longitud");
                tipo = hashmap.get("tipo");
                id = hashmap.get("id");

                marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

                if(tipo.equals("NoiseLevelObserved")){
                    googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        }
    }

}
