package services;

import java.util.ArrayList;
import java.util.HashMap;

public class SDS_ServerNotAvailable extends SensorDataService {


    public SDS_ServerNotAvailable() {

    }

    public ArrayList<HashMap<String, String>> getSensorData(){

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
