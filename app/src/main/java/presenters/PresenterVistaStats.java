package presenters;

import android.content.Intent;
import android.view.MenuItem;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;

import utilities.Interfaces_MVP;

public class PresenterVistaStats implements Interfaces_MVP.PresenterStats {

    // View reference.
    private Interfaces_MVP.ViewStats mView;


    public PresenterVistaStats(Interfaces_MVP.ViewStats view){
        mView = view;
    }


    @Override
    public boolean menu(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_home_stats:
                Intent goFavs = new Intent(mView.getActivityContext(), VistaFavoritos.class);
                mView.getActivityContext().startActivity(goFavs);
                return true;
            case R.id.stats_temp:
                //sensores de temperatura
                mView.seleccionarTipoAMostrar("Temperatura");
                return true;
            case R.id.stats_noise:
                //sensores de ruido
                mView.seleccionarTipoAMostrar("Ruido");
                return true;
            case R.id.stats_luz:
                //sensores de luz
                mView.seleccionarTipoAMostrar("Luz");
                return true;
            default:
                return false;
        }
    }


    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.ViewStats getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }
}
