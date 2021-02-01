package utilities;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import datos.SensorAmbiental;

import static android.content.ContentValues.TAG;

public class TipoMapa {

    private ArrayList<SensorAmbiental> sensorAmbList;

    public TipoMapa(ArrayList<SensorAmbiental> sensorAmbList) {
        this.sensorAmbList = sensorAmbList;
    }

    public void mapaCompleto(GoogleMap googleMap){
        String latitud;
        String longitud;
        String tipo;
        String id;
        LatLng marcador = null;

        googleMap.clear();

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

        googleMap.clear();

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

        googleMap.clear();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SensorAmbiental> filtrarListaParaMapa(LocalDate fechaSeleccionada){

        ArrayList<SensorAmbiental> listaFiltrada = new ArrayList<>();
        listaFiltrada = sensorAmbList;
        ArrayList<SensorAmbiental> listaEliminar = new ArrayList<>();

        LocalDate dateSensor;

        for(SensorAmbiental s: listaFiltrada){
            int fechaYear = Integer.parseInt(s.getUltModificacion().substring(0,4));
            int fechaMes = Integer.parseInt(s.getUltModificacion().substring(5,7));
            int fechaDia = Integer.parseInt(s.getUltModificacion().substring(8,10));
            dateSensor = LocalDate.of(fechaYear, fechaMes, fechaDia);

            Log.d(TAG, "Year calendario: "+fechaSeleccionada);
            Log.d(TAG, "Year sensor: "+dateSensor);

            //CompareTo < 0 --> menor la primera fecha
            if(dateSensor.compareTo(fechaSeleccionada)<0){
                listaEliminar.add(s);
            }
        }

        listaFiltrada.removeAll(listaEliminar);

        return listaFiltrada;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mapaFiltroFecha(GoogleMap googleMap, LocalDate result){
        String latitud;
        String longitud;
        String tipo;
        String id;
        LatLng marcador = null;

        Log.d(TAG, "Date Year calendario en mapafiltrofecha: "+ result);


        ArrayList<SensorAmbiental> listaFiltrada = filtrarListaParaMapa(result);

        if(googleMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            googleMap.clear();

            for(SensorAmbiental s: listaFiltrada) {
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




    }
}
