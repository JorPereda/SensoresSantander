package tasks;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;

import datos.Alarma;
import datos.AlarmaRegistrada;
import utilities.Interfaces_MVP;

public class LimpiezaAlarmasTask extends AsyncTask<Void, ArrayList<AlarmaRegistrada>, ArrayList<AlarmaRegistrada>> {

    private Alarma alarma;
    private Interfaces_MVP.ViewFavoritosYAlarma mView;

    public LimpiezaAlarmasTask(Interfaces_MVP.ViewFavoritosYAlarma mView, Alarma alarma) {
        this.alarma = alarma;
        this.mView = mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected ArrayList<AlarmaRegistrada> doInBackground(Void... voids) {
        ArrayList<AlarmaRegistrada> alarmasRegistradas = alarma.getAlarmasRegistradas();
        AlarmaRegistrada alarmaRegistrada = null;
        boolean borrar = false;
        int dia = LocalDateTime.now().getDayOfMonth();
        for(AlarmaRegistrada al : alarmasRegistradas){
            alarmaRegistrada = al;
            //if(Integer.parseInt(al.getFecha().substring(0,1))<dia-1){
            //    borrar = true;
            //}
            if((al.getFecha().substring(0,1)).equals("08")){
                borrar = true;
            }
        }
        if(borrar){
            alarmasRegistradas.remove(alarmaRegistrada);
        }
        return alarmasRegistradas;
    }

    @Override
    protected void onPostExecute(ArrayList<AlarmaRegistrada> alarmasRegistradas){
        alarma.setAlarmasRegistradas(alarmasRegistradas);
        //mView.updateAlarmInList(alarma);
    }
}
