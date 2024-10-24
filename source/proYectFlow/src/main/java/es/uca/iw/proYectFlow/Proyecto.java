package es.uca.iw.proYectFlow;

import java.math.BigDecimal;
import java.util.UUID;

public class Proyecto {
    UUID id = UUID.randomUUID();
    String nombre;
    String descripcion;
    BigDecimal coste;
    BigDecimal aportacionInicial;
    double puntuacionEstrategica;
    double puntuacionTecnica;
    double puntuacionAval;
    Aval aval;

    //NO RECUERDO MÁS ATRIBUTOS
    public Aval getAval() {
        return aval;
    }

    //Este metodo tendria restricción
    public void setAval(Aval aval) {
        this.aval = aval;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCoste() {
        return coste;
    }

    public void setCoste(BigDecimal coste) {
        this.coste = coste;
    }

    public BigDecimal getAportacionInicial() {
        return aportacionInicial;
    }

    public void setAportacionInicial(BigDecimal aportacionInicial) {
        this.aportacionInicial = aportacionInicial;
    }

    public double getPuntuacionEstrategica() {
        return puntuacionEstrategica;
    }

    //ESTE METODO TENDRIA EL @ROLE O COMO SE HAGA DEL CIO
    public void setPuntuacionEstrategica(double puntuacionEstrategica) {
        this.puntuacionEstrategica = puntuacionEstrategica;
    }

    public double getPuntuacionTecnica() {
        return puntuacionTecnica;
    }

    ////ESTE METODO TENDRIA EL @ROLE O COMO SE HAGA DEL OTP
    public void setPuntuacionTecnica(double puntuacionTecnica) {
        this.puntuacionTecnica = puntuacionTecnica;
    }


}
