package com.example.sensorsantander;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import Utilities.DetectConnection;

public class MenuPrincipal extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        // Get the application context
        mContext = getApplicationContext();

        Button irMapa = findViewById(R.id.button_mapa);

        irMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent (v.getContext(), VistaMapa.class);

                //Mensaje si no hay conexion
                AlertDialog.Builder builderSinRed = new AlertDialog.Builder(v.getContext());
                builderSinRed.setMessage("Esta intentando acceder al mapa sin ningún tipo de conexión.\nPor favor, habilite la red y vuelva a intentarlo.");
                builderSinRed.setCancelable(true);

                builderSinRed.setNeutralButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alertSinRed = builderSinRed.create();

                //Mensaje si no hay wifi
                AlertDialog.Builder builderSinWifi = new AlertDialog.Builder(v.getContext());
                builderSinWifi.setMessage("Esta intentando acceder al mapa mediante una conexión de datos.\nEs recomendable que acceda mediante wifi.");
                builderSinWifi.setCancelable(true);

                builderSinWifi.setPositiveButton(
                        "Continuar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivityForResult(intent, 0);
                            }
                        });

                builderSinWifi.setNegativeButton(
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertSinWifi = builderSinWifi.create();


                //Resto de casos de conexion
                if (DetectConnection.checkInternetConnection(mContext).equals("No net")) {
                    alertSinRed.show();
                } else if (DetectConnection.checkInternetConnection(mContext).equals("Wifi")){
                    startActivityForResult(intent, 0);
                } else if (DetectConnection.checkInternetConnection(mContext).equals("Data")){
                    alertSinWifi.show();

                }
            }
        });

    }
}
