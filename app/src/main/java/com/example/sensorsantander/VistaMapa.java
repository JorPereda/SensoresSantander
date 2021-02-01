package com.example.sensorsantander;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import datos.SensorAmbiental;
import presenters.PresenterVistaMapa;
import utilities.CustomMarkerInfoWindowView;
import utilities.Interfaces_MVP;
import utilities.TipoMapa;

import static android.content.ContentValues.TAG;

public class VistaMapa extends AppCompatActivity  implements Interfaces_MVP.ViewMapa, GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    private static Interfaces_MVP.PresenterMapa mPresenter;

    static ArrayList<SensorAmbiental> sensorAmbList = new ArrayList<>();

    private GoogleMap map;

    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mapa);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        sensorAmbList = (ArrayList<SensorAmbiental>) intent.getSerializableExtra("listaSensores");
        mPresenter = new PresenterVistaMapa(this, sensorAmbList);

    }

    @Override
    protected void onStop() {
        super.onStop();
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setInfoWindowAdapter(new CustomMarkerInfoWindowView(this));
        googleMap.clear();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String identificador;
                SensorAmbiental sensor = new SensorAmbiental();
                Intent intent = new Intent(VistaMapa.this, VistaDetallada.class);
                for (SensorAmbiental s : sensorAmbList) {
                    identificador = s.getTipo() + s.getIdentificador();
                    if(identificador.equals(marker.getTitle())){
                        sensor = s;
                    }
                }
                intent.putExtra("sensor", sensor);
                startActivity(intent);
            }
        });

        mapaTotal(googleMap);
    }

    public void mapaTotal(GoogleMap googleMap) {
        String latitud;
        String longitud;
        String tipo;
        String id;
        LatLng marcador = null;

        googleMap.clear();

        for (SensorAmbiental s : sensorAmbList) {
            latitud = s.getLatitud();
            longitud = s.getLongitud();
            tipo = s.getTipo();
            id = s.getIdentificador();

            marcador = new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));

            if (tipo.equals("WeatherObserved")) {
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            if (tipo.equals("NoiseLevelObserved")) {
                googleMap.addMarker(new MarkerOptions().position(marcador).title(tipo + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

        }

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(marcador)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dialogFiltrarFechas(){
        long fechaDelCalendario;

        final Integer[] fecha = new Integer[3];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View customLayout = getLayoutInflater().inflate(R.layout.filter_mapa_fecha_dialog, null);
        builder.setView(customLayout);

        final CalendarView simpleCalendarView = (CalendarView) customLayout.findViewById(R.id.simpleCalendarView); // get the reference of CalendarView

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Button botonAceptar = customLayout.findViewById(R.id.button_ok_filtro_fecha);

        final TipoMapa tipo = new TipoMapa(sensorAmbList);

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                String date = String.valueOf(year) +"-"+ String.valueOf(month+1) +"-"+ String.valueOf(dayOfMonth);
                LocalDate fechaSeleccionada = LocalDate.parse(date, formatter);
                //fechaDelCalendario = fechaSeleccionada.toEpochDay();
                setSelectedDate(fechaSeleccionada);
            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fechaDelCalendario = simpleCalendarView.getDate();
                //setSelectedDate(simpleCalendarView.getDate()); // get selected date in milliseconds
                //Log.d(TAG, "Date Year calendario: "+ getSelectedDate());
                tipo.mapaFiltroFecha(map, getSelectedDate());
                //LocalDate fechaSeleccionada = Instant.ofEpochMilli(simpleCalendarView.getDate()).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
                Log.d(TAG, "Date Fecha calendario: "+ getSelectedDate());
                //setSelectedDate(selectedDate);
                dialog.dismiss();
            }
        });
        Log.d(TAG, "Date Year calendario fuera de boton: "+ getSelectedDate());

        //TipoMapa tipo = new TipoMapa(sensorAmbList);
        //return getSelectedDate();

    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuMapa(item, map);
    }


}
