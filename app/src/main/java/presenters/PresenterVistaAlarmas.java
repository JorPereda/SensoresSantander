package presenters;

import android.content.Intent;
import android.view.MenuItem;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;


import utilities.Interfaces_MVP;


public class PresenterVistaAlarmas implements Interfaces_MVP.PresenterAlarma {

    // View reference.
    private Interfaces_MVP.ViewFavoritosYAlarma mView;


    public PresenterVistaAlarmas(Interfaces_MVP.ViewFavoritosYAlarma view){
        mView = view;
    }


    @Override
    public boolean menuAlarmas(MenuItem item) {

        if (item.getItemId() == R.id.action_home_alarma) {
            Intent goFavs = new Intent(mView.getActivityContext(), VistaFavoritos.class);
            goFavs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mView.getActivityContext().startActivity(goFavs);
            return true;
        }
        return false;
    }


    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.ViewFavoritosYAlarma getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }
}