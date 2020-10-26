package presenters;

import utilities.Interfaces_MVP;

public class PresenterVistaAlarmas implements Interfaces_MVP.PresenterAlarma {

    // View reference.
    private Interfaces_MVP.ViewFavoritosYAlarma mView;


    public PresenterVistaAlarmas(Interfaces_MVP.ViewFavoritosYAlarma view){
        mView = view;
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