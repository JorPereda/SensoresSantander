package presenters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;
import com.example.sensorsantander.VistaMapa;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import services.SensorDataService;
import utilities.Interfaces_MVP;
import utilities.TinyDB;


public class PresenterVistaFavoritos implements Interfaces_MVP.ProvidedPresenterFavoritosOps, Interfaces_MVP.RequiredPresenterOps {

    // View reference.
    private Interfaces_MVP.RequiredViewFavoritosOps mView;

    // Model reference (o service)
    private Interfaces_MVP.ProvidedModelOps svc;



    private ArrayList<SensorAmbiental> sensorAmbList;
    private ArrayList<Parent> parents;

    public PresenterVistaFavoritos(){
        sensorAmbList = new ArrayList<>();
    }

    public PresenterVistaFavoritos(Interfaces_MVP.RequiredViewFavoritosOps view){
        mView = view;
        sensorAmbList = new ArrayList<>();
        parents = new ArrayList<>();
        //TinyDB tinydb = new TinyDB(mView.getAppContext());
        //parents = tinydb.getListParent("parents");
    }

    public PresenterVistaFavoritos(Interfaces_MVP.ProvidedModelOps svc){
        this.svc = svc;
        sensorAmbList = new ArrayList<>();
        parents = new ArrayList<>();
        //parents = VariablesGlobales.parents;
    }

    /**
     * @return  Application context
     */
    @Override
    public Context getAppContext() {
        try {
            return getView().getAppContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * @return  Activity context
     */
    @Override
    public Context getActivityContext() {
        try {
            return getView().getActivityContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ArrayList<SensorAmbiental> getSensorAmbList() {
        return sensorAmbList;
    }

    public void setSensorAmbList(ArrayList<SensorAmbiental> sensorAmbList) {
        this.sensorAmbList = sensorAmbList;
    }

    @Override
    public ArrayList<SensorAmbiental> showSensorData(){
        getSensorData();
        return sensorAmbList;
    }

    @Override
    public void getSensorData() {
        sensorAmbList = new SensorDataService().getSensorData();
    }



    @Override
    public void showServerNotAvailable() {

    }

    @Override
    public void showConnectionNotAvailable() {

    }

    @Override
    public boolean menuFavoritos(MenuItem item, Activity activity){

        switch (item.getItemId()) {
            case R.id.irMapa:

                Intent abrirMapa = new Intent(mView.getActivityContext(), VistaMapa.class);
                mView.getActivityContext().startActivity(abrirMapa);
                return true;

            case R.id.action_add_element:

                final AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
                builder.setTitle("Introduce el nombre del nuevo grupo:");

                // Set up the input
                final EditText inputGrupo = new EditText(mView.getActivityContext());
                // Specify the type of input expected; this
                inputGrupo.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputGrupo);

                // Set up the buttons
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = inputGrupo.getText().toString();
                        Parent grupo = new Parent(m_Text);
                        mView.addToGroup(grupo);
                        VariablesGlobales.nombreGrupos.add(m_Text);
                        TinyDB tinydb = new TinyDB(mView.getAppContext());
                        tinydb.putListString("nombreGrupos", VariablesGlobales.nombreGrupos);

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;

            case R.id.editMode:

                mView.actionModeEditar();

                return true;

            default:

                return false;
        }

    }

    /**
     * Return the View reference.
     * Throw an exception if the View is unavailable.
     */
    private Interfaces_MVP.RequiredViewFavoritosOps getView() throws NullPointerException{
        if ( mView != null )
            return mView;
        else
            throw new NullPointerException("View is unavailable");
    }

    @Override
    public void onClickAddFavorito(final SensorAmbiental sensor, final String grupo){

        TinyDB tinydb = new TinyDB(mView.getAppContext());
        parents = tinydb.getListParent("parents");

        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
        builder.setTitle("Introduce el nombre del nuevo sensor:");

        // Set up the input
        final EditText inputSensor = new EditText(mView.getActivityContext());
        // Specify the type of input expected; this
        inputSensor.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputSensor);

        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = inputSensor.getText().toString();
                sensor.setTitulo(m_Text);

                for (Parent p : parents){
                    if(p.getNombre().equals(grupo)){
                        p.addChild(sensor);
                    }
                    TinyDB tinydb = new TinyDB(mView.getAppContext());
                    tinydb.putListParent("parents", parents);

                    Intent volverALista = new Intent(mView.getActivityContext(), VistaFavoritos.class);
                    mView.getActivityContext().startActivity(volverALista);
                }

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();



    }

}
