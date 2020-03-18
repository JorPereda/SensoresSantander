package Utilities;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerResponse {

    private static final String TAG = ServerResponse.class.getSimpleName();
    ArrayList<HashMap<String, String>> sensorAmbList;

    public ArrayList<HashMap<String, String>> getResponse(){

        sensorAmbList = new ArrayList<>();
        HttpHandler sh = new HttpHandler();

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

            int responseCode = httpConnection.getResponseCode();

            //HTTP: 200
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Response from url: " + jsonStr);
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

                            // tmp hash map for single sensor
                            HashMap<String, String> sensor = new HashMap<>();

                            // adding each child node to HashMap key => value
                            sensor.put("id", id);
                            sensor.put("tipo", tipo);
                            sensor.put("ruido", ruido);
                            sensor.put("luminosidad", luminosidad);
                            sensor.put("temperatura", temperatura);
                            sensor.put("bateria", bateria);
                            sensor.put("latitud", latitud);
                            sensor.put("longitud", longitud);
                            sensor.put("ultModificacion", ultMod);
                            sensor.put("uri", uri);

                            // adding sensor to sensor list
                            sensorAmbList.add(sensor);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                //HTTP: 400 Bad Request. El servidor no puede o no va a procesar el request por un error de sintaxis del cliente.
            } else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){

                //HTTP: 403 Forbidden. El request fue válido pero el servidor se niega a responder.
            } else if(responseCode == HttpURLConnection.HTTP_FORBIDDEN){

                //HTTP: 404 Not Found. El recurso del request no se ha podido encontrar pero podría estar disponible en el futuro.
                // Se permiten requests subsecuentes por parte del cliente.
            } else  if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){

                //HTTP: 500  Internal Server Error. Error genérico, cuando se ha dado una condición no esperada y no se puede concretar el mensaje.
            } else if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sensorAmbList;
    }

}
