package datos;

import java.io.Serializable;

public class Alarma implements Serializable {

    private SensorAmbiental sensor;
    private String tipoAlarma;
    private Double valorAlarma;
    private String nombre;

    public Alarma(){

    }

    public Alarma(SensorAmbiental sensor, String tipoAlarma, Double valorAlarma, String nombre) {
        this.sensor = sensor;
        this.tipoAlarma = tipoAlarma;
        this.valorAlarma = valorAlarma;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SensorAmbiental getSensor() {
        return sensor;
    }

    public void setSensor(SensorAmbiental sensor) {
        this.sensor = sensor;
    }

    public String getTipoAlarma() {
        return tipoAlarma;
    }

    public void setTipoAlarma(String tipoAlarma) {
        this.tipoAlarma = tipoAlarma;
    }

    public Double getValorAlarma() {
        return valorAlarma;
    }

    public void setValorAlarma(Double valorAlarma) {
        this.valorAlarma = valorAlarma;
    }
}
