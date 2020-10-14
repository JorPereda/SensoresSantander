package services;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import datos.SensorAmbiental;

public class SDS_ServerNotAvailable extends SensorDataService {


    public SDS_ServerNotAvailable(Context context) {
        super(context);

    }

    public ArrayList<SensorAmbiental> getSensorData(){

        try {
            throw new ServerNotAvailable();
        } catch (ServerNotAvailable serverNotAvailable) {
            serverNotAvailable.printStackTrace();
        }

        return sensorAmbList;
    }

    private class ServerNotAvailable extends Throwable {
    }
}
