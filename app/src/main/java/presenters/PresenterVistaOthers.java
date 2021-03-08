package presenters;

import android.content.Intent;
import android.view.MenuItem;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;

import utilities.Interfaces_MVP;

public class PresenterVistaOthers implements Interfaces_MVP.PresenterOthers {

    // View reference.
    private Interfaces_MVP.ViewOthers mView;


    public PresenterVistaOthers(Interfaces_MVP.ViewOthers view){
        mView = view;
    }


    @Override
    public boolean menu(MenuItem item) {

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
    private Interfaces_MVP.ViewOthers getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }
}
