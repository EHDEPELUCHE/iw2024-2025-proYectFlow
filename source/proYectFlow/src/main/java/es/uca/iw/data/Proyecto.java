package es.uca.iw.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;


@Entity
public class Proyecto extends AbstractEntity {

    String nombre, descripcion, interesados, alcance;
    Date fecha, fechaSolicitud;
    BigDecimal coste, aportacionInicial;
    double puntuacionEstrategica, puntuacionTecnica, puntuacionAval;
    Blob memoria;
    @ManyToOne
    @JoinColumn(name = "aval_id")
    Usuario promotor;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    Usuario solicitante;
    Estado estado;
    //List<Integer> ObjEstrategicos;

    public Proyecto(String nombre, String descripcion, String interesados, String alcance, BigDecimal coste, BigDecimal aportacionInicial,
                    Usuario aval, Usuario solicitante/*List<int> ObjEstrategicos*/) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.interesados = interesados;
        this.alcance = alcance;
        this.coste = coste;
        this.aportacionInicial = aportacionInicial;
        this.puntuacionEstrategica = -1;
        this.puntuacionTecnica = -1;
        this.puntuacionAval = -1;
        setAval(aval);
        estado = Estado.pedido;
        this.solicitante = solicitante;
        fechaSolicitud = new Date();
        //this.ObjEstrategicos = ObjEstrategicos;
    }

    public Proyecto() {

    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /*
    public List<int> getObjEstrategicos() {
        return ObjEstrategicos;
    }

    public void setObjEstrategicos(Collection<Integer> ObjEstrategicos) {
        this.ObjEstrategicos = ObjEstrategicos;
    }*/

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

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
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
        return promotor;
    }

    //Este metodo tendria restricción
    public void setAval(Usuario aval) {
        if (aval.getTipo() == Roles.PROMOTOR)
            this.promotor = aval;
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
