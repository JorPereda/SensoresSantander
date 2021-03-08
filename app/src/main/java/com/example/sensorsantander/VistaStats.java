package com.example.sensorsantander;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import adapters.MedidasCursorAdapter;
import baseDeDatos.EstadisticasContract;
import baseDeDatos.EstadisticasDbHelper;
import baseDeDatos.Medidas;
import baseDeDatos.MedidasController;
import datos.SensorAmbiental;
import presenters.PresenterVistaAlarmas;
import presenters.PresenterVistaOthers;
import services.AlarmasKeepRunningService;
import services.EstadisticasService;
import utilities.Interfaces_MVP;

public class VistaStats extends AppCompatActivity implements Interfaces_MVP.ViewOthers{

    private static Interfaces_MVP.PresenterOthers mPresenter;

    private SensorAmbiental sensor;

    private MedidasController mMedidasController;
    private ArrayList<Double> temperaturas = new ArrayList<>();
    private ArrayList<Double> ruidos = new ArrayList<>();
    private ArrayList<Double> luminosidades = new ArrayList<>();

    Intent serviceStatsIntent;
    private EstadisticasService serviceStats;
    private int intervalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_stats);

        mPresenter = new PresenterVistaOthers(this);
        mMedidasController = new MedidasController(this);

        Intent intent = getIntent();
        sensor = (SensorAmbiental) intent.getSerializableExtra("sensor");
        intervalo = intent.getIntExtra("Intervalo",0);
        Log.d("VistaEstadisticas ", "SensorStats: " + sensor.getIdentificador() + " " + sensor.getTitulo());


        if (sensor != null) {
            temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensor(sensor.getIdentificador()));
            ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensor(sensor.getIdentificador()));
            luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensor(sensor.getIdentificador()));
            //temperaturas = convertirArraytoDouble(mMedidasController.obtenerTemperaturasSensor("1"));
            //ruidos = convertirArraytoDouble(mMedidasController.obtenerRuidoSensor("1"));
            //luminosidades = convertirArraytoDouble(mMedidasController.obtenerLuzSensor("1"));
        }

        DescriptiveStatistics tempStats = new DescriptiveStatistics();
        DescriptiveStatistics ruidoStats = new DescriptiveStatistics();
        DescriptiveStatistics luzStats = new DescriptiveStatistics();
        Median mediana = new Median();

        for(Double d : temperaturas){
            tempStats.addValue(d);
        }
        for(Double d : ruidos){
            ruidoStats.addValue(d);
        }
        for(Double d : luminosidades){
            luzStats.addValue(d);
        }

        TextView tvSensor = findViewById(R.id.tvStatsName);

        TextView tvTempMedia = findViewById(R.id.tvTempMediaValor);
        TextView tvTempMediana = findViewById(R.id.tvTempMedianaValor);
        TextView tvTempMax = findViewById(R.id.tvTempMaxValor);
        TextView tvTempMin = findViewById(R.id.tvTempMinValor);
        TextView tvTempDesv = findViewById(R.id.tvTempDesvValor);

        TextView tvRuidoMedia = findViewById(R.id.tvRuidoMediaValor);
        TextView tvRuidoMediana = findViewById(R.id.tvRuidoMedianaValor);
        TextView tvRuidoMax = findViewById(R.id.tvRuidoMaxValor);
        TextView tvRuidoMin = findViewById(R.id.tvRuidoMinValor);
        TextView tvRuidoDesv = findViewById(R.id.tvRuidoDesvValor);

        TextView tvLuzMedia = findViewById(R.id.tvLuzMediaValor);
        TextView tvLuzMediana = findViewById(R.id.tvLuzMedianaValor);
        TextView tvLuzMax = findViewById(R.id.tvLuzMaxValor);
        TextView tvLuzMin = findViewById(R.id.tvLuzMinValor);
        TextView tvLuzDesv = findViewById(R.id.tvLuzDesvValor);

        tvSensor.setText(sensor.getTitulo());

        tvTempMedia.setText(String.valueOf(tempStats.getMean()));
        //tvTempMediana.setText(String.valueOf(mediana.evaluate(tempMedianas)));
        tvTempMax.setText(String.valueOf(tempStats.getMax()));
        tvTempMin.setText(String.valueOf(tempStats.getMin()));
        tvTempDesv.setText(String.valueOf(tempStats.getStandardDeviation()));

        tvRuidoMedia.setText(String.valueOf(ruidoStats.getMean()));
        //tvRuidoMediana.setText(String.valueOf(calculaMediana(ruidos)));
        tvRuidoMax.setText(String.valueOf(ruidoStats.getMax()));
        tvRuidoMin.setText(String.valueOf(ruidoStats.getMin()));
        tvRuidoDesv.setText(String.valueOf(ruidoStats.getStandardDeviation()));

        tvLuzMedia.setText(String.valueOf(luzStats.getMean()));
        //tvLuzMediana.setText(String.valueOf(calculaMediana(luminosidades)));
        tvLuzMax.setText(String.valueOf(luzStats.getMax()));
        tvLuzMin.setText(String.valueOf(luzStats.getMin()));
        tvLuzDesv.setText(String.valueOf(luzStats.getStandardDeviation()));


        Log.d("VistaStats ", "Medidas id 1 Nombre: " + sensor.getTitulo());

        serviceStats = new EstadisticasService();
        serviceStatsIntent = new Intent(this, serviceStats.getClass());
        if (!isMyServiceRunning(serviceStats.getClass())) {
            serviceStatsIntent.setAction(Intent.ACTION_VIEW);
            serviceStatsIntent.putExtra("Intervalo", intervalo);
            serviceStatsIntent.putExtra("sensor", sensor);
            startService(serviceStatsIntent);
        }

        ArrayList<Medidas> medidasTotales = mMedidasController.obtenerMedidasTotales();
        for(Medidas m : medidasTotales){
            Log.d("VistaEstadisticas ", "Medidas id: " + m.getId());
            Log.d("VistaEstadisticas ", "Medidas id sensor: " + m.getIdSensor());
            Log.d("VistaEstadisticas ", "Medidas temp: " +m.getTemperatura());
            Log.d("VistaEstadisticas ", "Medidas ruido: " + m.getRuido());
            Log.d("VistaEstadisticas ", "Medidas luz: " + m.getLuz());
            Log.d("VistaEstadisticas ", "Medidas fecha: " + m.getFecha());
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarmas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menu(item);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}
