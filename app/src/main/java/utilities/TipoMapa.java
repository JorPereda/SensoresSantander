package utilities;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public ArrayList<SensorAmbiental> filtrarListaParaMapa(long result){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(result);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar myCal = Calendar.getInstance();
        myCal.set(Calendar.YEAR, mYear);
        myCal.set(Calendar.MONTH, mMonth);
        myCal.set(Calendar.DAY_OF_MONTH, mDay);
        Date fechaCalendario = myCal.getTime();

        ArrayList<SensorAmbiental> listaFiltrada = new ArrayList<>();
        listaFiltrada = sensorAmbList;
        ArrayList<SensorAmbiental> listaEliminar = new ArrayList<>();

        Calendar myCalSensor = Calendar.getInstance();

        for(SensorAmbiental s: listaFiltrada){
            int fechaYear = Integer.parseInt(s.getUltModificacion().substring(0,4));
            int fechaMes = Integer.parseInt(s.getUltModificacion().substring(5,7));
            int fechaDia = Integer.parseInt(s.getUltModificacion().substring(8,10));
            myCalSensor.set(Calendar.YEAR, fechaYear);
            myCalSensor.set(Calendar.MONTH, fechaMes);
            myCalSensor.set(Calendar.DAY_OF_MONTH, fechaDia);
            Date fechaSensor = myCalSensor.getTime();
            //Log.d(TAG, "Year calendario: "+String.valueOf(mYear));
            //Log.d(TAG, "Year sensor: "+String.valueOf(fechaYear));
            //CompareTo < 0 --> menor la primera fecha
            if(fechaSensor.compareTo(fechaCalendario)<0){
                listaEliminar.add(s);
            }
        }

        listaFiltrada.removeAll(listaEliminar);

        return listaFiltrada;
    }

    public void mapaFiltroFecha(GoogleMap googleMap, long result){
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
