package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utilities.Interfaces_MVP;

public class SDS_ServerNotAvailable extends SensorDataService {

    /**
     * Main constructor, called by Activity during MVP setup
     *
     * @param presenter Presenter instance
     */
    public SDS_ServerNotAvailable(Interfaces_MVP.RequiredPresenterOps presenter) {
        super(presenter);
    }

    public ArrayList<HashMap<String, String>> getSensorData(){

        try {
            throw new ServerNotAvailable();
        } catch (ServerNotAvailable serverNotAvailable) {
            serverNotAvailable.printStackTrace();
        }

        return null;
    }

    private class ServerNotAvailable extends Throwable {
    }
}
