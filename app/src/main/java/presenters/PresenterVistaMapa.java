package presenters;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Calendar;

import datos.SensorAmbiental;
import utilities.Interfaces_MVP;
import utilities.TipoMapa;

import static android.content.ContentValues.TAG;

public class PresenterVistaMapa implements Interfaces_MVP.PresenterMapa {

    // View reference.
    private Interfaces_MVP.ViewMapa mView;

    // Model reference (o service)
    private Interfaces_MVP.ProvidedModelOps svc;


    private ArrayList<SensorAmbiental> sensorAmbList;

    public PresenterVistaMapa(Interfaces_MVP.ViewMapa view, ArrayList<SensorAmbiental> sensores){
        mView = view;
        sensorAmbList = sensores;
    }


    @Override
    public void showServerNotAvailable() {

    }

    @Override
    public void showConnectionNotAvailable() {

    }

    @Override
    public boolean menuMapa(MenuItem item, GoogleMap map) {
        TipoMapa tipo = new TipoMapa(sensorAmbList);

        switch (item.getItemId()) {
            /*case R.id.action_refresh:
                Toast.makeText(mView.getActivityContext(), "Refresh selected", Toast.LENGTH_SHORT).show();
                tipo.mapaCompleto(map);
                try {
                    new GetDataTotalTask(mView.).execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;*/
            case R.id.filtro_fecha:
                //long fechaSeleccionada =
                mView.dialogFiltrarFechas();
                //tipo.mapaFiltroFecha(map, fechaSeleccionada);
                //Log.d(TAG, "Date Year calendario en case: "+ fechaSeleccionada);
                return true;
            case R.id.action_home:
                Intent goFavs = new Intent(mView.getActivityContext(), VistaFavoritos.class);
                mView.getActivityContext().startActivity(goFavs);
                return true;
            case R.id.todos:
                //todos los sensores
                tipo.mapaCompleto(map);
                return true;
            case R.id.weather:
                //sensores atmosfericos
                tipo.mapaWeather(map);
                return true;
            case R.id.noise:
                //sensores de ruido
                tipo.mapaRuido(map);
                return true;
            default:
                return menuMapa(item, map);
        }
    }


    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.ViewMapa getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }

}
