package datos;

import java.util.ArrayList;


//Clase para elemento padre en lista desplegable
public  class Parent{

    private String nombre;
    // ArrayList to store child objects
    private ArrayList<SensorAmbiental> children = new ArrayList<SensorAmbiental>();

    public Parent(String nombre) {
        this.nombre = nombre;
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


}

