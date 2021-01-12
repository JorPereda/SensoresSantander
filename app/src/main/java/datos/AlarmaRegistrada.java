package datos;

import java.io.Serializable;
import java.time.LocalDate;

public class AlarmaRegistrada implements Serializable {

    private Double valor;
    private String fecha;
    private String fechaReal;

    public AlarmaRegistrada(Double valor, String fecha) {
        this.valor = valor;
        this.fecha = fecha;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaReal() {
        return fechaReal;
    }

    public void setFechaReal(String fechaReal) {
        this.fechaReal = fechaReal;
    }
}
