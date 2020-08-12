package utilities;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import datos.SensorAmbiental;

public interface Interfaces_MVP {

    /**
     * Metodos ofrecidos a View para comunicar con el Presenter
     */
    interface ProvidedPresenterOps{
        ArrayList<SensorAmbiental> showSensorData();
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        boolean menuMapa(MenuItem item, GoogleMap map);
        boolean menuFavoritos(MenuItem item, Activity activity);
        void onClickAddFavorito(SensorAmbiental sensor, String grupo);
    }

    /**
     * Metodos requeridos de Presenter disponibles para el Model
     */
    interface RequiredPresenterOps{
        Context getAppContext();
        Context getActivityContext();
        void getSensorData() throws AccessDeniedException;
    }

    /**
     * Metodos View requeridos para el Presenter
     */
    interface RequiredViewOps{
        Context getAppContext();
        Context getActivityContext();
        void addToGroup(CustomExpandableListAdapter.Parent grupo);
        void reloadAdapter();
    }

    /**
     * Metodos ofrecidos a Model para comunicarse con el Presenter
     */
    interface ProvidedModelOps{

    }

}
