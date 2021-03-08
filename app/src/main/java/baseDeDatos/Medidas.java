package baseDeDatos;

import android.content.ContentValues;

/**
 * Entidad medidas
 */
public class Medidas {

    private long id;
    private Integer idSensor;
    private String fecha;
    private String temp;
    private String ruido;
    private String luz;

    public Medidas(Integer idSensor, String fecha, String temp, String ruido, String luz) {
        //this.id = id;
        this.idSensor = idSensor;
        this.fecha = fecha;
        this.temp = temp;
        this.ruido = ruido;
        this.luz = luz;
    }

    public Medidas(long id, Integer idSensor, String fecha, String temp, String ruido, String luz) {
        this.id = id;
        this.idSensor = idSensor;
        this.fecha = fecha;
        this.temp = temp;
        this.ruido = ruido;
        this.luz = luz;
    }

    public long getId() {
        return id;
    }

    public Integer getIdSensor() {
        return idSensor;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTemperatura() {
        return temp;
    }

    public String getRuido() {
        return ruido;
    }

    public String getLuz() {
        return luz;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(EstadisticasContract.MedidasSensorEntry.ID, id);
        values.put(EstadisticasContract.MedidasSensorEntry.ID_SENSOR, idSensor);
        values.put(EstadisticasContract.MedidasSensorEntry.FECHA, fecha);
        values.put(EstadisticasContract.MedidasSensorEntry.TEMP, temp);
        values.put(EstadisticasContract.MedidasSensorEntry.RUIDO, ruido);
        values.put(EstadisticasContract.MedidasSensorEntry.LUZ, luz);
        return values;
    }

}
