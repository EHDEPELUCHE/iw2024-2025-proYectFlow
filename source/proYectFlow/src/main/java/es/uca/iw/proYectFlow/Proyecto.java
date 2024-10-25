package es.uca.iw.proYectFlow;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class Proyecto {
    UUID id = UUID.randomUUID();

    String nombre;
    String descripcion;
    String interesados;
    String alcance;
    Date fecha;
    Date fechaSolicitud;
    BigDecimal coste;
    BigDecimal aportacionInicial;
    double puntuacionEstrategica;
    double puntuacionTecnica;
    double puntuacionAval;
    Usuario aval;
    Usuario solicitante;
    Estado estado;
    Collection<Integer> ObjEstrategicos;

    public Proyecto(String nombre, String descripcion, String interesados, String alcance, BigDecimal coste, BigDecimal aportacionInicial,
                    double puntuacionAval, double puntuacionEstrategica, Usuario aval, Usuario solicitante, Collection<Integer> ObjEstrategicos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.interesados = interesados;
        this.alcance = alcance;
        this.coste = BigDecimal.valueOf(0.0);
        this.aportacionInicial = aportacionInicial;
        this.puntuacionEstrategica = -1;
        this.puntuacionTecnica = -1;
        this.puntuacionAval = -1;
        setAval(aval);
        estado = Estado.pedido;
        this.solicitante = solicitante;
        fechaSolicitud = new Date();
        this.ObjEstrategicos = ObjEstrategicos;

    }

    public Collection<Integer> getObjEstrategicos() {
        return ObjEstrategicos;
    }

    public void setObjEstrategicos(Collection<Integer> ObjEstrategicos) {
        this.ObjEstrategicos = ObjEstrategicos;
    }
    
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setFecha() {
        this.fecha = new Date();
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Estado getEstado() {
        return estado;
    }

    //El solicitante no lo puede usar
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    //NO RECUERDO MÁS ATRIBUTOS
    public Usuario getAval() {
        return aval;
    }

    //Este metodo tendria restricción
    public void setAval(Usuario aval) {
        if (aval.getTipo() == Usuario.Tipo.Promotor)
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

    public enum Estado {pedido, avalado, evaluadotecnicamente, evaluadoestrategicamente, Enproceso, Denegado}


}
