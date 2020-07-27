package datos;

import java.util.ArrayList;
import java.util.List;

public class GroupListFavoritos {

    public String nombreGrupo;
    public List<SensorAmbiental> childrenSensorList = new ArrayList<SensorAmbiental>();

    public GroupListFavoritos(String nombre) {
        this.nombreGrupo = nombre;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public List<SensorAmbiental> getChildrenSensorList() {
        return childrenSensorList;
    }

    public void setChildrenSensorList(List<SensorAmbiental> childrenSensorList) {
        this.childrenSensorList = childrenSensorList;
    }



}
