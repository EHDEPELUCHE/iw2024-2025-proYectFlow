package es.uca.iw.Proyecto;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.checkerframework.common.aliasing.qual.NonLeaked;
import org.checkerframework.common.aliasing.qual.Unique;

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
