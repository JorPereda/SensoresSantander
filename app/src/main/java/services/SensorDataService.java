package services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import datos.SensorAmbiental;
import utilities.HttpHandler;
import utilities.Interfaces_MVP;


public class SensorDataService implements Interfaces_MVP.ProvidedModelOps {

    private static final String tag = SensorDataService.class.getSimpleName();

    ArrayList<SensorAmbiental> sensorAmbList;

    Context context;

    public SensorDataService(Context context) {
        this.context = context;
    }

    public ArrayList<SensorAmbiental> getSensorData(){

        HttpHandler sh = new HttpHandler();
        int responseCode = -1;

        // Making a request to url and getting response
        String url = "http://datos.santander.es/api/rest/datasets/sensores_smart_env_monitoring.json";
        String jsonStr = sh.makeServiceCall(url);

        try {

            URL mUrl = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();

            //HTTP: 200
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Se inicializa la lista una vez sabido que el codigo es correcto, para comprobar que la lista es null en caso contrario
                sensorAmbList = new ArrayList<>();
                Log.e(tag, "Response from url: " + jsonStr);
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray sensores = jsonObj.getJSONArray("resources");

                        // looping through All sensors
                        for (int i = 0; i < sensores.length(); i++) {
                            JSONObject s = sensores.getJSONObject(i);
                            String id = s.getString("dc:identifier");
                            String tipo = s.getString("ayto:type");
                            String ruido = s.getString("ayto:noise");
                            String luminosidad = s.getString("ayto:light");
                            String temperatura = s.getString("ayto:temperature");
                            String bateria = s.getString("ayto:battery");
                            String latitud = s.getString("ayto:latitude");
                            String longitud = s.getString("ayto:longitude");
                            String ultMod = s.getString("dc:modified");
                            String uri = s.getString("uri");

                            SensorAmbiental sensorAmb = new SensorAmbiental();

                            sensorAmb.setIdentificador(id);
                            sensorAmb.setTipo(tipo);
                            sensorAmb.setRuido(ruido);
                            sensorAmb.setLuminosidad(luminosidad);
                            sensorAmb.setTemperatura(temperatura);
                            sensorAmb.setBattery(bateria);
                            sensorAmb.setLatitud(latitud);
                            sensorAmb.setLongitud(longitud);
                            sensorAmb.setUltModificacion(ultMod);
                            sensorAmb.setUri(uri);

                            // adding sensor to sensor list
                            sensorAmbList.add(sensorAmb);
                        }
                    } catch (final JSONException e) {
                        Log.e(tag, "Json parsing error: " + e.getMessage());
                    }
                //HTTP: 400 Bad Request. El servidor no puede o no va a procesar el request por un error de sintaxis del cliente.
                //No deberia producirse por tener url fija
            } else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){

                //HTTP: 403 Forbidden. El request fue válido pero el servidor se niega a responder.
            } else if(responseCode == HttpURLConnection.HTTP_FORBIDDEN){

                //HTTP: 404 Not Found. El recurso del request no se ha podido encontrar pero podría estar disponible en el futuro.
                // Se permiten requests subsecuentes por parte del cliente.
            } else  if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){

                //HTTP: 500  Internal Server Error. Error genérico, cuando se ha dado una condición no esperada y no se puede concretar el mensaje.
                //No deberia producirse por tener url fija
            } else if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){

            }

        } catch (IOException e) {
            Log.e(tag, "IOException: " + e.getMessage());
        } catch (Exception ex) {
            Log.e(tag, "Exception: " + ex.getMessage());
        }

        return sensorAmbList;
    }

}
