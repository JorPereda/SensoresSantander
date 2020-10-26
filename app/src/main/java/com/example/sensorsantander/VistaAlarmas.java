package com.example.sensorsantander;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

import datos.Alarma;
import datos.Parent;
import datos.SensorAmbiental;
import presenters.PresenterVistaFavoritos;
import tasks.CompruebaAlarmaTask;
import utilities.Interfaces_MVP;
import adapters.ListAlarmasAdapter;
import utilities.TinyDB;

public class VistaAlarmas extends AppCompatActivity implements Interfaces_MVP.ViewFavoritosYAlarma {

    private static final String CHANNEL_ID = "1";
    private ListView listaAlarmasListView;
    private ListAlarmasAdapter mAdapter;
    private ArrayList<Alarma> listaAlarmas;
    private ArrayList<Alarma> alarmasSaltadas;
    private TextView mTextView;


    private static Interfaces_MVP.PresenterFavoritos mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_alarmas);

        mPresenter = new PresenterVistaFavoritos(this);

        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");

        listaAlarmasListView = findViewById(R.id.lista_alarmas);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        listaAlarmasListView.setEmptyView(emptyText);

        mAdapter = new ListAlarmasAdapter(listaAlarmas, this);
        listaAlarmasListView.setAdapter(mAdapter);

        mTextView = findViewById(R.id.alarma_activada_nombre1);
        for(Alarma alarma : listaAlarmas){
           // new CompruebaAlarmaTask(alarma, mTextView).execute();
        }
        createNotificationChannel();
        addNotification();
    }

    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_alarm)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(8, builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nombre del canal";
            String description = "Descripcion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        TinyDB tinydb = new TinyDB(this);
        tinydb.putListAlarmas("alarmas", listaAlarmas);
    }


    @Override
    protected void onStart() {
        super.onStart();
        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinyDB tinydb = new TinyDB(this);
        listaAlarmas = tinydb.getListAlarmas("alarmas");
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public Context getActivityContext() {
        return getApplicationContext();
    }

    @Override
    public void addToGroup(Parent grupo) {

    }

    @Override
    public PresenterVistaFavoritos getPresenter() {
        return (PresenterVistaFavoritos) mPresenter;
    }

    @Override
    public boolean checkItemList(long packedPosition) {
        return false;
    }

    @Override
    public void actionModeEditar() {

    }

    @Override
    public ExpandableListView getExpList() {
        return null;
    }

    @Override
    public void updateListAlarmas(ArrayList<Alarma> alarmas) {
        this.listaAlarmas = alarmas;
    }

    @Override
    public void updateListTotal(ArrayList<SensorAmbiental> sensorAmbList) {

    }
}