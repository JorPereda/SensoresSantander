package utilities;

import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Interfaces_MVP {

    /**
     * Metodos ofrecidos a View para comunicar con el Presenter
     */
    interface ProvidedPresenterOps{
        ArrayList<HashMap<String, String>> showSensorData();
        void showServerNotAvailable();
        void showConnectionNotAvailable();
        boolean menuOptionsClicked(MenuItem item, GoogleMap map);

    }

    /**
     * Metodos requeridos de Presenter disponibles para el Model
     */
    interface RequiredPresenterOps{
        void getSensorData() throws AccessDeniedException;
    }

    /**
     * Metodos View requeridos para el Presenter
     */
    interface RequiredViewOps{

    }

    /**
     * Metodos ofrecidos a Model para comunicarse con el Presenter
     */
    interface ProvidedModelOps{

    }

}
