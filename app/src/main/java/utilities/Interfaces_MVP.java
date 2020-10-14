package utilities;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import datos.Parent;
import datos.SensorAmbiental;
import presenters.PresenterVistaFavoritos;

public interface Interfaces_MVP {

    /**
     * Metodos ofrecidos a View para comunicar con el Presenter
     */
    interface ProvidedPresenterFavoritosOps {
        ArrayList<SensorAmbiental> showSensorData();
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        boolean menuFavoritos(MenuItem item, Activity activity);
        void onClickAddFavorito(SensorAmbiental sensor, String grupo);
        void onClickAddAlarma(SensorAmbiental sensor, Double valor, String tipo, String nombre);
    }

    interface ProvidedPresenterMapaOps{
        ArrayList<SensorAmbiental> showSensorData();
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        boolean menuMapa(MenuItem item, GoogleMap map);
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
    interface RequiredViewFavoritosOps{
        Context getAppContext();
        Context getActivityContext();
        void addToGroup(Parent grupo);
        PresenterVistaFavoritos getPresenter();
        void actionModeEditar();
    }

    interface RequiredViewMapaOps{
        Context getAppContext();
        Context getActivityContext();
    }

    /**
     * Metodos ofrecidos a Model para comunicarse con el Presenter
     */
    interface ProvidedModelOps{

    }

}
