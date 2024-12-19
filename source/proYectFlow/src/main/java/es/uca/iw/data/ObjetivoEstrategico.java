package es.uca.iw.data;

import jakarta.persistence.Entity;

@Entity
public class ObjetivoEstrategico extends AbstractEntity{
    private String Objetivo;
    public String getObjetivo() {
        return Objetivo;
    }
    public void setObjetivo(String objetivo) {
        Objetivo = objetivo;
    }
}
