package es.uca.iw.proyecto;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class AlineamientoEstrategico extends AbstractEntity {

    @NotEmpty(message = "Por favor, introduzca un objetivo para los proyectos")
    @Column(unique = true, nullable = false)
    private String Objetivo;

    public String getObjetivo() {
        return Objetivo;
    }

    public void setObjetivo(String objetivo) {
        Objetivo = objetivo;
    }
}
