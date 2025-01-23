package es.uca.iw.proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.global.AbstractEntity;
import es.uca.iw.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representa una entidad de proyecto con varios atributos como nombre, descripción, interesados, alcance, costos y más.
 * Esta entidad es auditada y extiende de AbstractEntity.
 * <p>
 * Atributos:
 * - nombre: El nombre del proyecto. Debe ser único y no vacío.
 * - descripcion: La descripción del proyecto. No debe estar vacía.
 * - interesados: Los interesados del proyecto. No debe estar vacío.
 * - alcance: El alcance del proyecto. No debe estar vacío.
 * - fechaLimite: La fecha límite para el proyecto. Puede ser nula.
 * - fechaSolicitud: La fecha en que se solicitó el proyecto.
 * - aportacionInicial: La contribución inicial para el proyecto. No debe ser nula.
 * - coste: El costo del proyecto. No debe ser nulo.
 * - puntuacionEstrategica: La puntuación estratégica del proyecto. Por defecto es -1 indicando no evaluado.
 * - puntuacionTecnica: La puntuación técnica del proyecto. Por defecto es -1 indicando no evaluado.
 * - puntuacionAval: La puntuación de aval del proyecto. Por defecto es -1 indicando no evaluado.
 * - memoria: El blob de memoria del proyecto. Puede ser nulo.
 * - promotor: El promotor (aval) del proyecto. Puede ser nulo.
 * - solicitante: El solicitante del proyecto. Puede ser nulo.
 * - jefe: El jefe del proyecto. Puede ser nulo.
 * - director: El director del proyecto.
 * - gradoAvance: El grado de avance del proyecto.
 * - estado: El estado del proyecto, representado por el enum Estado.
 * - convocatoria: La convocatoria a la que pertenece el proyecto. No debe ser nula.
 * <p>
 * Métodos:
 * - Getters y setters para todos los atributos.
 * - getPdf(): Devuelve un InputStream del blob de memoria PDF.
 * - getPdfNombre(): Devuelve el nombre del archivo PDF.
 * - equals(Object other): Verifica la igualdad basada en el ID del proyecto.
 * - hashCode(): Devuelve el código hash para el proyecto.
 * <p>
 * Enum Estado:
 * - Representa los varios estados en los que un proyecto puede estar: SOLICITADO, AVALADO, EVALUADO_TECNICAMENTE, EVALUADO_ESTRATEGICAMENTE, NO_EN_DESARROLLO, EN_DESARROLLO, DENEGADO, FINALIZADO.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(
        name = "Proyecto.detail",
        attributeNodes = {
                @NamedAttributeNode("promotor"),
                @NamedAttributeNode("solicitante"),
                @NamedAttributeNode("jefe"),
                @NamedAttributeNode("convocatoria")
        }
)
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

    @ManyToOne
    @JoinColumn(name = "jefe_id", nullable = true)
    Usuario jefe;

    String director;

    Double gradoAvance;

    @Enumerated(EnumType.ORDINAL)
    Estado estado;

    @Column(nullable = true)
    Integer recHumanos;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id", nullable = false)
    private Convocatoria convocatoria;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @CreatedBy
    private Usuario createdBy;

    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    @LastModifiedBy
    private Usuario lastModifiedBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;

    public Proyecto(String nombre, String descripcion, String interesados, String alcance, BigDecimal coste,
                    BigDecimal aportacionInicial, Usuario aval, Usuario solicitante, Date fechaLimite, Blob memoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.interesados = interesados;
        this.alcance = alcance;
        this.coste = coste;
        this.aportacionInicial = aportacionInicial;
        this.puntuacionEstrategica = -1; // -1 indica que no se ha evaluado
        this.puntuacionTecnica = -1;
        this.puntuacionAval = -1;
        this.promotor = aval;
        this.estado = Estado.SOLICITADO;
        this.solicitante = solicitante;
        this.fechaLimite = fechaLimite;
        this.fechaSolicitud = new Date();
        this.memoria = memoria;
        gradoAvance = 0.0;
    }

    public Proyecto() {
        // Empty constructor
    }

    public Usuario getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Usuario lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
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

    public Usuario getJefe() {
        return jefe;
    }

    public void setJefe(Usuario jefe) {
        this.jefe = jefe;
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

    public Double getGradoAvance() {
        return gradoAvance;
    }

    public void setGradoAvance(Double gradoAvance) {
        this.gradoAvance = gradoAvance;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getRecHumanos() {
        return recHumanos;
    }

    public void setRecHumanos(Integer recHumanos) {
        this.recHumanos = recHumanos;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Proyecto)) return false;
        return getId() != null && getId().equals(((Proyecto) other).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public enum Estado {SOLICITADO, AVALADO, EVALUADO_TECNICAMENTE, EVALUADO_ESTRATEGICAMENTE, NO_EN_DESARROLLO, EN_DESARROLLO, DENEGADO, FINALIZADO}
}
