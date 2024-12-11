package es.uca.iw.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;
import java.io.ByteArrayInputStream;

@Entity
public class Proyecto extends AbstractEntity {
    @NotEmpty
    @Column(unique = true, nullable = false)
    String nombre;

    @NotEmpty
    @Column(nullable = false)
    String descripcion, interesados, alcance;

    @Column(nullable = true)
    Date fechaLimite;

    @NotEmpty
    @Column(nullable = false)
    Date fechaSolicitud;
    
    @NotEmpty
    @Column(nullable = false)
    BigDecimal coste, aportacionInicial;

    double puntuacionEstrategica, puntuacionTecnica, puntuacionAval;
    
    @NotEmpty
    @Column(nullable = false)
    Blob memoria;

    @ManyToOne
    @JoinColumn(name = "aval_id")
    Usuario promotor;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    Usuario solicitante;
    
    Estado estado;
    
    //List<Integer> ObjEstrategicos; CAMBIAR

    public enum Estado {solicitado, avalado, evaluadoTecnicamente, evaluadoEstrategicamente, aceptado, enDesarollo, denegado}

    public Proyecto(String nombre, String descripcion, String interesados, String alcance, BigDecimal coste, 
                    BigDecimal aportacionInicial, Usuario aval, Usuario solicitante, Date fechaLimite, Blob memoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.interesados = interesados;
        this.alcance = alcance;
        this.coste = coste;
        this.aportacionInicial = aportacionInicial;
        // Estos valores a -1 para indicar que no se han asignado aun
        this.puntuacionEstrategica = -1;
        this.puntuacionTecnica = -1;
        this.puntuacionAval = -1;
        this.promotor = aval;
        this.estado = Estado.solicitado;
        this.solicitante = solicitante;
        this.fechaLimite = fechaLimite;
        this.fechaSolicitud = new Date();
        this.memoria = (Blob) memoria;
        //this.ObjEstrategicos = ObjEstrategicos; CAMBIAR
    }

    // Constructor vacio, CAMBIAR
    public Proyecto() {}

    /* CAMBIAR
    public List<int> getObjEstrategicos() {
        return ObjEstrategicos;
    }

    public void setObjEstrategicos(Collection<Integer> ObjEstrategicos) {
        this.ObjEstrategicos = ObjEstrategicos;
    }*/

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getInteresados() { return interesados; }
    
    public void setInteresados (String interesados) { this.interesados = interesados; }
    
    public String getAlcance() { return alcance; }

    public void setAlcance (String alcance) { this.alcance = alcance; }

    public Date getFechaLimite() { return fechaLimite; }

    public void setFechaLimite(Date fecha) { this.fechaLimite = fecha; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    
    public void setFechaSolicitud() { this.fechaSolicitud = new Date(); }

    public BigDecimal getCoste() { return coste; }

    public void setCoste(BigDecimal coste) { this.coste = coste; }

    public BigDecimal getAportacionInicial() { return aportacionInicial; }

    public void setAportacionInicial(BigDecimal aportacionInicial) { this.aportacionInicial = aportacionInicial; }

    public double getPuntuacionEstrategica() { return puntuacionEstrategica; }

    public void setPuntuacionEstrategica(double puntuacionEstrategica) { this.puntuacionEstrategica = puntuacionEstrategica; }

    public double getPuntuacionTecnica() { return puntuacionTecnica; }

    public void setPuntuacionTecnica(double puntuacionTecnica) { this.puntuacionTecnica = puntuacionTecnica; }

    public double getPuntuacionAval() { return puntuacionAval; }

    public void setPuntuacionAval(double puntuacionAval) { this.puntuacionAval = puntuacionAval; }

    public Blob getMemoria() { return memoria; }

    public void setMemoria(Blob memoria) { this.memoria = memoria; }

    public Usuario getPromotor(){ return promotor; }
    
    public void setPromotor(Usuario usuario) { this.promotor = usuario; }

    public Usuario getSolicitante() { return solicitante; }

    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }

    public Estado getEstado() { return estado; }

    public void setEstado(Estado estado) { this.estado = estado; }
   
    public InputStream getPdf() {
        try {
            if (memoria != null) return memoria.getBinaryStream();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public String getPdfNombre() {
        try {
            if (memoria != null) {
                String[] parts = memoria.getBinaryStream().toString().split("/");
                return parts[parts.length - 1];
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
