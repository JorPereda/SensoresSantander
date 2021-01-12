package datos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Alarma implements Serializable {

    private SensorAmbiental sensor;
    private String tipoAlarma;
    private String maxMin;
    private Double valorAlarma;
    private String nombre;

    private ArrayList<AlarmaRegistrada> alarmasRegistradas;

    private static final AtomicInteger count = new AtomicInteger(0);
    private int idAlarma;

    public Alarma(SensorAmbiental sensor, String tipoAlarma, String maxMin, Double valorAlarma, String nombre) {
        this.sensor = sensor;
        this.tipoAlarma = tipoAlarma;
        this.maxMin = maxMin;
        this.valorAlarma = valorAlarma;
        this.nombre = nombre;
        this.idAlarma = count.incrementAndGet();
        this.alarmasRegistradas = new ArrayList<>();
    }

    public int getIdAlarma() {
        return idAlarma;
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

    public String getMaxMin() {
        return maxMin;
    }

    public void setMaxMin(String maxMin) {
        this.maxMin = maxMin;
    }

    public Double getValorAlarma() {
        return valorAlarma;
    }

    public void setValorAlarma(Double valorAlarma) {
        this.valorAlarma = valorAlarma;
    }

    public ArrayList<AlarmaRegistrada> getAlarmasRegistradas() {
        return alarmasRegistradas;
    }

    public void setAlarmasRegistradas(ArrayList<AlarmaRegistrada> alarmasRegistradas) {
        this.alarmasRegistradas = alarmasRegistradas;
    }
}
