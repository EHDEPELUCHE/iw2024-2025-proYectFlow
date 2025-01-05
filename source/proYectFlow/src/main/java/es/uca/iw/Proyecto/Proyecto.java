package es.uca.iw.Proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
public class Proyecto extends AbstractEntity {
    private static final Logger logger = Logger.getLogger(Proyecto.class.getName());

    @NotEmpty(message = "Por favor, introduzca un nombre para el proyecto")
    @Column(unique = true, nullable = false)
    String nombre;

    @NotEmpty(message = "Por favor, rellene el campo requerido")
    @Column(nullable = false)
    String descripcion, interesados, alcance;

    @Column(nullable = true)
    Date fechaLimite;

    Date fechaSolicitud;

    @NotNull(message = "Por favor, rellene el campo requerido")
    @Column(nullable = false)
    BigDecimal aportacionInicial, coste;

    double puntuacionEstrategica, puntuacionTecnica, puntuacionAval;

    /* @NotEmpty
     @Column(nullable = false)*/
    Blob memoria;

    @ManyToOne
    @JoinColumn(name = "aval_id", nullable = true)
    Usuario promotor;

    @ManyToOne
    @JoinColumn(name = "solicitante_id", nullable = true)
    Usuario solicitante;

    @Enumerated(EnumType.ORDINAL)
    Estado estado;

    @ManyToMany
    List<AlineamientoEstrategico> ObjEstrategicos;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id", nullable = false)
    private Convocatoria convocatoria;

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
    public Proyecto() {
    }

    public String getNombre() {
        return nombre;
    }

    //CAMBIAR
    public List<AlineamientoEstrategico> getObjEstrategicos() {
        return ObjEstrategicos;
    }

    public void setObjEstrategicos(List<AlineamientoEstrategico> ObjEstrategicos) {
        this.ObjEstrategicos = ObjEstrategicos;
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

    public String getInteresados() {
        return interesados;
    }

    public void setInteresados(String interesados) {
        this.interesados = interesados;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fecha) {
        this.fechaLimite = fecha;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud() {
        this.fechaSolicitud = new Date();
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

    public void setPuntuacionEstrategica(double puntuacionEstrategica) {
        this.puntuacionEstrategica = puntuacionEstrategica;
    }

    public double getPuntuacionTecnica() {
        return puntuacionTecnica;
    }

    public void setPuntuacionTecnica(double puntuacionTecnica) {
        this.puntuacionTecnica = puntuacionTecnica;
    }

    public double getPuntuacionAval() {
        return puntuacionAval;
    }

    public void setPuntuacionAval(double puntuacionAval) {
        this.puntuacionAval = puntuacionAval;
    }

    public Blob getMemoria() {
        return memoria;
    }

    public void setMemoria(Blob memoria) {
        this.memoria = memoria;
    }

    public Usuario getPromotor() {
        return promotor;
    }

    public void setPromotor(Usuario usuario) {
        this.promotor = usuario;
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

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public InputStream getPdf() {
        try {
            if (memoria != null) {
                return memoria.getBinaryStream();
            } else {
                logger.warning("No se puede obtener el PDF.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al acceder la memoria.", e);
        }
        return null;
    }

    public String getPdfNombre() {
        try {
            if (memoria != null) {
                String[] parts = memoria.getBinaryStream().toString().split("/");
                return parts[parts.length - 1];
            } else {
                logger.warning("No se puede obtener el PDF.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al acceder a la memoria.", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ocurrió un error inesperado al procesar el PDF.", e);
        }
        return null;
    }

    public Convocatoria getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(Convocatoria convocatoria) {
        this.convocatoria = convocatoria;
    }

    public enum Estado {solicitado, avalado, evaluadoTecnicamente, evaluadoEstrategicamente, noenDesarrollo, enDesarrollo, denegado}
}
