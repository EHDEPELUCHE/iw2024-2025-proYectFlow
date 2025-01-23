package es.uca.iw.proyecto;

import es.uca.iw.global.AbstractEntity;
import es.uca.iw.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * La clase AlineamientoEstrategico representa una entidad de alineamiento estratégico.
 * Extiende la clase AbstractEntity e incluye campos para un objetivo y un estado activo.
 * Esta clase está anotada con anotaciones JPA para mapearla a una tabla de base de datos.
 * <p>
 * Anotaciones:
 * - @Entity: Especifica que la clase es una entidad y se mapea a una tabla de base de datos.
 * - @EntityListeners(AuditingEntityListener.class): Especifica el listener de callback para propósitos de auditoría.
 * <p>
 * Campos:
 * - objetivo: Una cadena única y no nula que representa el objetivo para los proyectos.
 * - activo: Un booleano que indica si el alineamiento estratégico está activo.
 * <p>
 * Constructores:
 * - AlineamientoEstrategico(String objetivo): Inicializa la entidad con el objetivo dado y establece activo a true.
 * - AlineamientoEstrategico(): Constructor por defecto.
 * <p>
 * Métodos:
 * - getObjetivo(): Devuelve el objetivo.
 * - setObjetivo(String objetivo): Establece el objetivo.
 * - getActivo(): Devuelve el estado activo.
 * - setActivo(Boolean activo): Establece el estado activo.
 * - equals(Object o): Compara esta entidad con otro objeto para igualdad.
 * - hashCode(): Devuelve el valor del código hash para esta entidad.
 * <p>
 * Validación:
 * - @NotEmpty(message = "Por favor, introduzca un objetivo para los proyectos"): Asegura que el campo objetivo no esté vacío.
 * - @Column(unique = true, nullable = false): Asegura que el campo objetivo sea único y no nulo en la base de datos.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AlineamientoEstrategico extends AbstractEntity {
    @NotEmpty(message = "Por favor, introduzca un objetivo para los proyectos")
    @Column(unique = true, nullable = false)
    private String objetivo;

    private Boolean activo;

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

    public AlineamientoEstrategico(String objetivo) {
        this.objetivo = objetivo;
        this.activo = true;
    }

    public AlineamientoEstrategico() {
        //Empty constructor
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

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlineamientoEstrategico)) return false;
        if (!super.equals(o)) return false;

        AlineamientoEstrategico that = (AlineamientoEstrategico) o;

        if (getObjetivo() != null ? !getObjetivo().equals(that.getObjetivo()) : that.getObjetivo() != null)
            return false;
        return getActivo() != null ? getActivo().equals(that.getActivo()) : that.getActivo() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getObjetivo() != null ? getObjetivo().hashCode() : 0);
        return result;
    }
}
