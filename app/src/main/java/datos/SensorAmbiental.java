package datos;

import java.io.Serializable;

public class SensorAmbiental implements Serializable {

    private String identificador;
    private String battery;
    private String temperatura; //grados celsius
    private String luminosidad; //lumenes
    private String ruido;       //decibelios
    private String tipo;       //WeatherObserved o NoiseLevelObserved
    private String latitud;
    private String longitud;
    private String ultModificacion;
    private String uri;

    private String titulo;
    private String direccion;

    private int intervaloStatsMuestreo;
    private int intervaloStatsTCalculo;
    private int intervaloStatsTVida;

    public SensorAmbiental(){

    }

    public SensorAmbiental(String identificador, String battery, String temperatura, String luminosidad,
                           String ruido, String tipo, String latitud, String longitud,
                           String ultModificacion, String uri) {
        this.identificador = identificador;
        this.battery = battery;
        this.temperatura = temperatura;
        this.luminosidad = luminosidad;
        this.ruido = ruido;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ultModificacion = ultModificacion;
        this.uri = uri;
        this.intervaloStatsMuestreo = 0;
        this.intervaloStatsTCalculo = 0;
        this.intervaloStatsTVida = 0;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getLuminosidad() {
        return luminosidad;
    }

    public void setLuminosidad(String luminosidad) {
        this.luminosidad = luminosidad;
    }

    public String getRuido() {
        return ruido;
    }

    public void setRuido(String ruido) {
        this.ruido = ruido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getUltModificacion() {
        return ultModificacion;
    }

    public void setUltModificacion(String ultModificacion) {
        this.ultModificacion = ultModificacion;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIntervaloStatsMuestreo() {
        return intervaloStatsMuestreo;
    }

    public void setIntervaloStatsMuestreo(int intervaloStatsMuestreo) {
        this.intervaloStatsMuestreo = intervaloStatsMuestreo;
    }

    public int getIntervaloStatsTCalculo() {
        return intervaloStatsTCalculo;
    }

    public void setIntervaloStatsTCalculo(int intervaloStatsTCalculo) {
        this.intervaloStatsTCalculo = intervaloStatsTCalculo;
    }

    public int getIntervaloStatsTVida() {
        return intervaloStatsTVida;
    }

    public void setIntervaloStatsTVida(int intervaloStatsTVida) {
        this.intervaloStatsTVida = intervaloStatsTVida;
    }
}
