package com.example.sensorsantander;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import utilities.DetectConnection;

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

                //Resto de casos de conexion
                if (DetectConnection.checkInternetConnection(mContext).equals("No net")) {
                    alertSinRed.show();
                } else {
                    startActivityForResult(intent, 0);
                }
            }
        });

    }
}
