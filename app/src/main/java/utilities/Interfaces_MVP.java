package utilities;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import datos.Alarma;
import datos.Parent;
import datos.SensorAmbiental;
import presenters.PresenterVistaFavoritos;

public interface Interfaces_MVP {

    /**
     * Metodos ofrecidos a View para comunicar con el Presenter
     */
    interface PresenterFavoritos {
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        void getListaSensores();
        boolean menuFavoritos(MenuItem item, Activity activity);
        void onClickAddFavorito(SensorAmbiental sensor, String grupo);
        void onClickAddAlarma(SensorAmbiental sensor, Double valor, String tipo, String maxMin, String nombre);
    }

    interface PresenterMapa {
        //ArrayList<SensorAmbiental> showSensorData();
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        boolean menuMapa(MenuItem item, GoogleMap map);
    }

    /**
     * Metodos requeridos de Presenter disponibles para el Model
     */
    interface PresenterAlarma {

    }

    /**
     * Metodos View requeridos para el Presenter
     */
    interface ViewFavoritosYAlarma {
        Context getAppContext();
        Context getActivityContext();
        void addToGroup(Parent grupo);
        PresenterVistaFavoritos getPresenter();
        boolean checkItemList(int indexChild, int indexGroup);
        void actionModeEditar();
        ExpandableListView getExpList();
        void updateParentInList(Parent parent);
        void updateListParents(ArrayList<Parent> parents);
        void updateListAlarmas(ArrayList<Alarma> alarmas);
        void updateAlarmInList(Alarma alarma);
        void updateListTotal(ArrayList<SensorAmbiental> sensorAmbList);
        void updateListView(ArrayList<Parent> parents);
    }

    interface ViewMapa {
        void dialogFiltrarFechas();
        Context getAppContext();
        Context getActivityContext();
    }

    /**
     * Metodos ofrecidos a Model para comunicarse con el Presenter
     */
    interface ProvidedModelOps{

    }

}
