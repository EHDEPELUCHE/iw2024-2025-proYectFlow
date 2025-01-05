package es.uca.iw.proyecto;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class AlineamientoEstrategico extends AbstractEntity {

    @NotEmpty(message = "Por favor, introduzca un objetivo para los proyectos")
    @Column(unique = true, nullable = false)
    private String objetivo;

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlineamientoEstrategico)) return false;
        if (!super.equals(o)) return false;

        AlineamientoEstrategico that = (AlineamientoEstrategico) o;

        return getObjetivo() != null ? getObjetivo().equals(that.getObjetivo()) : that.getObjetivo() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getObjetivo() != null ? getObjetivo().hashCode() : 0);
        return result;
    }
}
