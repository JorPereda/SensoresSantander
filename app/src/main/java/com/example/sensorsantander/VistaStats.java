package com.example.sensorsantander;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import baseDeDatos.Medidas;
import baseDeDatos.MedidasController;
import datos.SensorAmbiental;
import presenters.PresenterVistaStats;
import utilities.Interfaces_MVP;

public class VistaStats extends AppCompatActivity implements Interfaces_MVP.ViewStats {

    private static Interfaces_MVP.PresenterStats mPresenter;

    private SensorAmbiental sensor;

    private MedidasController mMedidasController;
    private ArrayList<Double> temperaturas = new ArrayList<>();
    private ArrayList<Double> ruidos = new ArrayList<>();
    private ArrayList<Double> luminosidades = new ArrayList<>();

    private TextView tvTempMedia;
    private TextView tvTempMediana;
    private TextView tvTempMax;
    private TextView tvTempMin;
    private TextView tvTempDesv;
    private TextView tvTipo;

    private Median mediana;
    private DescriptiveStatistics tempStats;
    private DescriptiveStatistics ruidoStats;
    private DescriptiveStatistics luzStats;

    private String tipoMostrado = "Temperatura";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_stats);

        mPresenter = new PresenterVistaStats(this);
        mMedidasController = new MedidasController(this);

        Intent intent = getIntent();
        sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");
        Log.d("VistaEstadisticas ", "SensorStats: " + sensor.getIdentificador() + " " + sensor.getTitulo());

        int tiempoCalculo = sensor.getIntervaloStatsTCalculo();
        LocalDateTime currentTime = LocalDateTime.now();
        String fechaHastaHora = currentTime.toString().substring(0,13);
        String fechaHastaDia = currentTime.toString().substring(0,10);
        String fechaHastaSemana = currentTime.minusWeeks(1).toString().substring(0,13);
        //0 = 1 hora
        if(tiempoCalculo==0){
            temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensorFecha(sensor.getIdentificador(), fechaHastaHora));
            ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensorFecha(sensor.getIdentificador(), fechaHastaHora));
            luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensorFecha(sensor.getIdentificador(), fechaHastaHora));
        }
        //1 = 1 dia
        if(tiempoCalculo==1){
            temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensorFecha(sensor.getIdentificador(), fechaHastaDia));
            ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensorFecha(sensor.getIdentificador(), fechaHastaDia));
            luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensorFecha(sensor.getIdentificador(), fechaHastaDia));
        }
        //2 = 1 semana
        if(tiempoCalculo==2){
            temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensorFecha(sensor.getIdentificador(), fechaHastaSemana));
            ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensorFecha(sensor.getIdentificador(), fechaHastaSemana));
            luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensorFecha(sensor.getIdentificador(), fechaHastaSemana));
        }

        /*temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensor(sensor.getIdentificador()));
        ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensor(sensor.getIdentificador()));
        luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensor(sensor.getIdentificador()));*/

        mediana = new Median();
        tempStats = new DescriptiveStatistics();
        ruidoStats = new DescriptiveStatistics();
        luzStats = new DescriptiveStatistics();

        TextView tvSensor = findViewById(R.id.tvStatsName);
        tvTipo = findViewById(R.id.tvTipoSensorStats);

        tvTempMedia = findViewById(R.id.tvTempMediaValor);
        tvTempMediana = findViewById(R.id.tvTempMedianaValor);
        tvTempMax = findViewById(R.id.tvTempMaxValor);
        tvTempMin = findViewById(R.id.tvTempMinValor);
        tvTempDesv = findViewById(R.id.tvTempDesvValor);

        tvSensor.setText(sensor.getTitulo());
        calculos(tipoMostrado);

        Log.d("VistaStats ", "Medidas id 1 Nombre: " + sensor.getTitulo());

        ArrayList<Medidas> medidasTotales = mMedidasController.obtenerMedidasTotales();
        for(Medidas m : medidasTotales){
            Log.d("VistaEstadisticas ", "Medidas id: " + m.getId());
            Log.d("VistaEstadisticas ", "Medidas id sensor: " + m.getIdSensor());
            Log.d("VistaEstadisticas ", "Medidas temp: " +m.getTemperatura());
            Log.d("VistaEstadisticas ", "Medidas ruido: " + m.getRuido());
            Log.d("VistaEstadisticas ", "Medidas luz: " + m.getLuz());
            Log.d("VistaEstadisticas ", "Medidas fecha: " + m.getFecha());
            Log.d("VistaEstadisticas ", "Medidas fecha cortada: " + m.getFechaCortada());
        }
        Log.d("VistaEstadisticas ", "size 1: " + mMedidasController.obtenerTemperaturasSensor(sensor.getIdentificador()).size());


        limpieza();

    }

    public void calculos(String tipo){
        Log.d("VistaEstadisticas ", "size 2: " + mMedidasController.obtenerTemperaturasSensor(sensor.getIdentificador()).size());
        Log.d("VistaEstadisticas ", "temp size 2: " + temperaturas.size());

        switch (tipo){
            case "Temperatura":
                temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensor(sensor.getIdentificador()));


                for(Double d : temperaturas){
                    tempStats.addValue(d);
                    tvTempMedia.setText(String.valueOf(tempStats.getMean()));
                    tvTempMediana.setText(String.valueOf(mediana.evaluate(tempStats.getValues())));
                    tvTempMax.setText(String.valueOf(tempStats.getMax()));
                    tvTempMin.setText(String.valueOf(tempStats.getMin()));
                    tvTempDesv.setText(String.valueOf(tempStats.getStandardDeviation()));
                    tvTipo.setText(tipo);
                    Log.d("VistaEstadisticas ", "tempStats: " + d.toString());
                }
                break;
            case "Ruido":
                ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensor(sensor.getIdentificador()));
                for(Double d : ruidos){
                    ruidoStats.addValue(d);
                    tvTempMedia.setText(String.valueOf(ruidoStats.getMean()));
                    tvTempMediana.setText(String.valueOf(mediana.evaluate(ruidoStats.getValues())));
                    tvTempMax.setText(String.valueOf(ruidoStats.getMax()));
                    tvTempMin.setText(String.valueOf(ruidoStats.getMin()));
                    tvTempDesv.setText(String.valueOf(ruidoStats.getStandardDeviation()));
                    tvTipo.setText(tipo);
                }
                break;
            case "Luz":
                luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensor(sensor.getIdentificador()));
                for(Double d : luminosidades){
                    luzStats.addValue(d);
                    tvTempMedia.setText(String.valueOf(luzStats.getMean()));
                    tvTempMediana.setText(String.valueOf(mediana.evaluate(luzStats.getValues())));
                    tvTempMax.setText(String.valueOf(luzStats.getMax()));
                    tvTempMin.setText(String.valueOf(luzStats.getMin()));
                    tvTempDesv.setText(String.valueOf(luzStats.getStandardDeviation()));
                    tvTipo.setText(tipo);
                }
                break;
        }

    }

    public ArrayList<Double> convertirArraytoDouble(ArrayList<String> medidas){
        ArrayList<Double> medidasNueva = new ArrayList<>();
        for(String medida : medidas){
            medidasNueva.add(ParseDouble(medida));
        }
        return medidasNueva;
    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void limpieza(){

        int intervaloLimpieza = sensor.getIntervaloStatsTVida();

        LocalDateTime currentTime = LocalDateTime.now();
        ArrayList<Medidas> medidasTotales = mMedidasController.obtenerMedidasTotales();
        for(Medidas m : medidasTotales) {
            LocalDateTime fechaSensor = LocalDateTime.parse(m.getFecha());
            if (intervaloLimpieza == 0) {
                if (fechaSensor.isBefore(currentTime.minusDays(1))) {
                    mMedidasController.eliminarMedida(m);
                }
            }
            if (intervaloLimpieza == 1) {
                if (fechaSensor.isBefore(currentTime.minusWeeks(1))) {
                    mMedidasController.eliminarMedida(m);
                }
            }
            if (intervaloLimpieza == 2) {
                if (fechaSensor.isBefore(currentTime.minusMonths(1))) {
                    mMedidasController.eliminarMedida(m);
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menu(item);
    }

    @Override
    public void seleccionarTipoAMostrar(String tipo) {
        this.tipoMostrado = tipo;
        calculos(tipo);
    }

    /*@Override
    public void filtrarResultFecha() {
        temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensorFecha(sensor.getIdentificador()));
        ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensorFecha(sensor.getIdentificador()));
        luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensorFecha(sensor.getIdentificador()));
    }*/

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }



}
