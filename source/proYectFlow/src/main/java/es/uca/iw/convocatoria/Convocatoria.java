package es.uca.iw.convocatoria;

import es.uca.iw.global.AbstractEntity;
import es.uca.iw.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Representa una entidad Convocatoria con detalles sobre una convocatoria específica.
 * Esta entidad incluye información como el presupuesto total, el presupuesto restante,
 * las fechas de inicio y fin, y si la convocatoria está activa.
 *
 * <p>Esta clase extiende {@link AbstractEntity} y está anotada con anotaciones JPA
 * para mapearla a una tabla de base de datos. También utiliza {@link AuditingEntityListener}
 * para propósitos de auditoría.</p>
 *
 * <p>Atributos:</p>
 * <ul>
 *   <li>{@code nombre} - El nombre de la convocatoria, generado en base a las fechas de inicio y fin.</li>
 *   <li>{@code presupuestototal} - El presupuesto total para la convocatoria. Este campo es obligatorio.</li>
 *   <li>{@code presupuestorestante} - El presupuesto restante para la convocatoria.</li>
 *   <li>{@code fechaLimite} - La fecha límite para la convocatoria.</li>
 *   <li>{@code fechaInicio} - La fecha de inicio de la convocatoria. Este campo es obligatorio.</li>
 *   <li>{@code fechaFinal} - La fecha de fin de la convocatoria. Este campo es obligatorio.</li>
 *   <li>{@code activa} - Indica si la convocatoria está actualmente activa.</li>
 * </ul>
 *
 * <p>Constructores:</p>
 * <ul>
 *   <li>{@link #Convocatoria(BigDecimal, Date, Date, Date, int)} - Construye una nueva Convocatoria con el presupuesto, fecha límite, fecha de inicio y fecha de fin especificados.</li>
 *   <li>{@link #Convocatoria()} - Constructor por defecto.</li>
 * </ul>
 *
 * <p>Métodos:</p>
 * <ul>
 *   <li>{@link #enPlazo()} - Verifica si la fecha actual está dentro del período de la convocatoria.</li>
 *   <li>{@link #getFechaFinal()} - Obtiene la fecha de fin de la convocatoria.</li>
 *   <li>{@link #setFechaFinal(Date)} - Establece la fecha de fin de la convocatoria, asegurando que sea posterior a la fecha límite.</li>
 *   <li>{@link #getFechaLimite()} - Obtiene la fecha límite de la convocatoria.</li>
 *   <li>{@link #setFechaLimite(Date)} - Establece la fecha límite de la convocatoria, asegurando que sea posterior a la fecha de inicio.</li>
 *   <li>{@link #getPresupuestototal()} - Obtiene el presupuesto total de la convocatoria.</li>
 *   <li>{@link #setPresupuestototal(BigDecimal)} - Establece el presupuesto total de la convocatoria.</li>
 *   <li>{@link #getFechaInicio()} - Obtiene la fecha de inicio de la convocatoria.</li>
 *   <li>{@link #setFechaInicio(Date)} - Establece la fecha de inicio de la convocatoria.</li>
 *   <li>{@link #getPresupuestorestante()} - Obtiene el presupuesto restante de la convocatoria.</li>
 *   <li>{@link #setPresupuestorestante(BigDecimal)} - Establece el presupuesto restante de la convocatoria.</li>
 *   <li>{@link #getActiva()} - Obtiene el estado activo de la convocatoria.</li>
 *   <li>{@link #setActiva(Boolean)} - Establece el estado activo de la convocatoria.</li>
 *   <li>{@link #getNombre()} - Obtiene el nombre de la convocatoria.</li>
 *   <li>{@link #setNombre()} - Establece el nombre de la convocatoria basado en las fechas de inicio y fin.</li>
 *   <li>{@link #equals(Object)} - Verifica si esta convocatoria es igual a otro objeto.</li>
 *   <li>{@link #hashCode()} - Devuelve el código hash para esta convocatoria.</li>
 * </ul>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Convocatoria extends AbstractEntity {

    @Column(nullable = true)
    String nombre;

    @NotNull(message = "Por favor, rellene el campo requerido")
    @Column(nullable = false)
    BigDecimal presupuestototal;

    BigDecimal presupuestorestante;

    Date fechaLimite;

    @Column(nullable = false)
    Date fechaInicio;

    @Column(nullable = false)
    Date fechaFinal;

    Boolean activa;

    Integer recHumanosDisponibles;

    Integer recHumanosRestantes;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @CreatedBy
    private Usuario createdBy;

    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    @LastModifiedBy
    private Usuario lastModifiedBy;

    public Convocatoria(BigDecimal presupuesto, Date fechaLimite, Date fechaInicio, Date fechaFinal, int recHumanosDisponibles) {
        this.presupuestototal = presupuesto;
        this.presupuestorestante = presupuesto;
        this.fechaInicio = fechaInicio;
        this.recHumanosDisponibles = recHumanosDisponibles;
        recHumanosRestantes = recHumanosDisponibles;
        setFechaLimite(fechaLimite);
        setFechaFinal(fechaFinal);
        setNombre();
    }

    public Convocatoria() {
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

    public boolean enPlazo() {
        Date hoy = new Date();
        return (hoy.after(fechaInicio) && hoy.before(fechaLimite));
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        if (fechaLimite != null && fechaFinal != null && !fechaLimite.before(fechaFinal)) {
            throw new IllegalArgumentException("La fecha final debe ser posterior a la fecha límite.");
        }
        this.fechaFinal = fechaFinal;
        setNombre();
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        if (fechaLimite != null && fechaInicio != null && !fechaInicio.before(fechaLimite)) {
            throw new IllegalArgumentException("La fecha límite debe ser posterior a la fecha de inicio.");
        }
        this.fechaLimite = fechaLimite;
    }

    public BigDecimal getPresupuestototal() {
        return presupuestototal;
    }

    public void setPresupuestototal(BigDecimal presupuestototal) {
        this.presupuestototal = presupuestototal;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
        setNombre();
    }

    public BigDecimal getPresupuestorestante() {
        return presupuestorestante;
    }

    public void setPresupuestorestante(BigDecimal presupuestorestante) {
        this.presupuestorestante = presupuestorestante;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public String getNombre() {
        return nombre;
    }

    private void setNombre() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        dateFormat.format(fechaInicio);
        dateFormat.format(fechaFinal);

        this.nombre = "Convocatoria " + dateFormat.format(fechaInicio) + " - " + dateFormat.format(fechaFinal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Convocatoria that = (Convocatoria) o;
        return presupuestototal.equals(that.presupuestototal) &&
                fechaInicio.equals(that.fechaInicio) &&
                fechaFinal.equals(that.fechaFinal);
    }

    public Integer getRecHumanosDisponibles() {
        return recHumanosDisponibles;
    }

    public void setRecHumanosDisponibles(Integer recHumanosDisponibles) {
        this.recHumanosDisponibles = recHumanosDisponibles;
    }

    public Integer getRecHumanosRestantes() {
        return recHumanosRestantes;
    }

    public void setRecHumanosRestantes(Integer recHumanosRestantes) {
        this.recHumanosRestantes = recHumanosRestantes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(presupuestototal, fechaInicio, fechaFinal);
    }
}
