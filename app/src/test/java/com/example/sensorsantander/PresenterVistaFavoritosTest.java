package com.example.sensorsantander;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import presenters.PresenterVistaFavoritos;
import services.SDS_AccessDenied;
import services.SDS_ServerNotAvailable;
import services.SensorDataService;


public class PresenterVistaFavoritosTest {

    PresenterVistaFavoritos pr;

    ArrayList<HashMap<String, String>> sensorAmbList;

    @Before
    public void setUp(){
        sensorAmbList = new ArrayList<>();
        HashMap<String, String> param_aux1 = new HashMap<>();

        param_aux1.put("type", "WeatherObserved");
        param_aux1.put("noise", "");
        param_aux1.put("temperature", "12.45");
        param_aux1.put("id", "150");

        sensorAmbList.add(param_aux1);

        HashMap<String, String> param_aux2 = new HashMap<>();

        param_aux2.put("type", "NoiseLevelObserved");
        param_aux2.put("noise", "50.0");
        param_aux2.put("temperature", "");
        param_aux2.put("id", "700");

        sensorAmbList.add(param_aux2);

    }

    @Test
    public void getSensorDataTest(){
        pr = new PresenterVistaFavoritos(new SensorDataService());
        pr.setSensorAmbList(sensorAmbList);
        if(pr.getSensorAmbList() == null){
            throw new NullPointerException("La lectura de los datos no se ha producido");
        }
    }

    @Test
    public void serverNotAvailableTest(){
        pr = new PresenterVistaFavoritos(new SDS_ServerNotAvailable());
        pr.getSensorData();
    }

    @Test
    public void accessDeniedTest(){
        pr = new PresenterVistaFavoritos(new SDS_AccessDenied());
        pr.getSensorData();
    }
}
