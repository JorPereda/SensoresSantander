package tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import datos.Alarma;
import datos.SensorAmbiental;
import utilities.Interfaces_MVP;

public class CompruebaAlarmaTask extends AsyncTask<Void, Boolean, Boolean> {

    private Alarma alarma;
    private SensorAmbiental sensor;
    private Boolean saltaAlarma;

    private Interfaces_MVP.ViewFavoritosYAlarma mView;

    private String tipo;
    private String maxMin;
    private Double valorAlarma;

    private WeakReference<TextView> mTextView;

    public CompruebaAlarmaTask(Alarma alarma) {
        this.alarma = alarma;
    }

    public CompruebaAlarmaTask(Alarma alarma, TextView tv) {
        this.alarma = alarma;
        mTextView = new WeakReference<>(tv);
    }

    @Override
    protected void onPreExecute() {
        saltaAlarma = false;
        sensor = alarma.getSensor();
        tipo = alarma.getTipoAlarma();
        maxMin = alarma.getMaxMin();
        valorAlarma = alarma.getValorAlarma();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if(maxMin.equals("min")){
            switch (tipo){
                case "temp":
                    //Compare to:
                    // <0 : numero1 menor que numero2
                    // >0 : numero1 mayor que numero2
                    if(Double.valueOf(sensor.getTemperatura()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                    }
                    break;
                case "luz":
                    if(Double.valueOf(sensor.getLuminosidad()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                    }
                    break;
                case "ruido":
                    if(Double.valueOf(sensor.getRuido()).compareTo(valorAlarma)<0){
                        saltaAlarma = true;
                    }
                    break;
            }
        }else if(maxMin.equals("max")){
            switch (tipo){
                case "temp":
                    //Compare to:
                    // <0 : numero1 menor que numero2
                    // >0 : numero1 mayor que numero2
                    if(Double.valueOf(sensor.getTemperatura()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                    }
                    break;
                case "luz":
                    if(Double.valueOf(sensor.getLuminosidad()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                    }
                    break;
                case "ruido":
                    if(Double.valueOf(sensor.getRuido()).compareTo(valorAlarma)>0){
                        saltaAlarma = true;
                    }
                    break;
            }
        }



        return saltaAlarma;
    }

    @Override
    protected void onPostExecute(Boolean saltaAlarma) {
        Log.e("Prueba de alarma: ", alarma.getNombre() + " con valor " + alarma.getMaxMin() + " = " + alarma.getValorAlarma());

        if (saltaAlarma){
            Log.e("Alarma saltada!!", "Ha saltado la alarma del sensor " + alarma.getNombre() + " con valor " + alarma.getMaxMin() + " = " + alarma.getValorAlarma());
            //mTextView.get().setText("Alarma activada.");
        }

    }
}
