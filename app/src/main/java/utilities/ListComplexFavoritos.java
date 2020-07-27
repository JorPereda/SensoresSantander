package utilities;

import java.util.ArrayList;

import datos.SensorAmbiental;

public class ListComplexFavoritos{

    ArrayList<SensorAmbiental> lista = new ArrayList<>();

    public ArrayList<SensorAmbiental> getLista() {
        return lista;
    }

    public void setLista(ArrayList<SensorAmbiental> lista) {
        this.lista = lista;
    }

}