package datos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


//Clase para elemento padre en lista desplegable
public  class Parent implements Serializable {

    private static final AtomicInteger count = new AtomicInteger(0);
    private int idParent;
    private String nombre;
    // ArrayList to store child objects
    private ArrayList<SensorAmbiental> children = new ArrayList<SensorAmbiental>();

    public Parent(String nombre) {
        this.idParent = count.incrementAndGet();
        this.nombre = nombre;
    }
    public int getIdParent() {
        return idParent;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<SensorAmbiental> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<SensorAmbiental> children) {
        this.children.addAll(children);
    }

    public SensorAmbiental getChild(int childPosition) {
        return children.get(childPosition);
    }

    public void addChild(SensorAmbiental childItem) {
        children.add(childItem);
    }

    public void removeChild(int childPosition) {
        children.remove(childPosition);
    }

    public void removeChild(SensorAmbiental child) {
        children.remove(child);
    }

    public void updateChild(SensorAmbiental child){
        for (SensorAmbiental sensor : children){
            if (sensor.getIdentificador().equals(child.getIdentificador())){
                children.set(children.indexOf(sensor), child);
            }
        }
    }


}

