package datos;

import java.util.Date;

public class AlarmaRegistrada {

    private Double valor;
    private String fecha;

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
}
