package es.uca.iw.Proyecto;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class AlineamientoEstrategico extends AbstractEntity {
    private String Objetivo;

    public String getObjetivo() {
        return Objetivo;
    }

    public void setObjetivo(String objetivo) {
        Objetivo = objetivo;
    }
}
