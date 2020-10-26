package tasks;

import android.os.AsyncTask;
import java.util.ArrayList;
import datos.SensorAmbiental;
import services.SensorDataService;
import utilities.Interfaces_MVP;

public class GetDataTotalTask extends AsyncTask<Void, Void, ArrayList<SensorAmbiental>> {

    private SensorDataService service;
    private Interfaces_MVP.ViewFavoritosYAlarma vistaFavoritos;

    public GetDataTotalTask(Interfaces_MVP.ViewFavoritosYAlarma vistaFavoritos) {
        this.vistaFavoritos = vistaFavoritos;
    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        service = new SensorDataService();
    }


    @Override
    protected ArrayList<SensorAmbiental> doInBackground(Void... voids) {
        return service.getSensorData();
     }

    @Override
    protected void onPostExecute(ArrayList<SensorAmbiental> result) {
        super.onPostExecute(result);
        vistaFavoritos.updateListTotal(result);
        vistaFavoritos.getPresenter().setSensorAmbList(result);
    }


}