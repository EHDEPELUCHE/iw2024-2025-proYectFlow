package es.uca.iw.Convocatoria;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Convocatoria extends AbstractEntity {

    BigDecimal presupuestototal ;
    Date fecha_limite;
    BigDecimal presupuestorestante ;
    Date fecha_inicio ;
    Date fecha_final ;
    Boolean activa;

    public Convocatoria(BigDecimal presupuesto, Date fecha_limite, Date fecha_inicio, Date fecha_final) {
        this.presupuestototal = presupuesto;
        this.fecha_limite = fecha_limite;
        this.fecha_inicio = fecha_inicio;
        this.presupuestorestante = presupuesto;
        this.fecha_final = fecha_final;
    }

    public Convocatoria() {

    }

    public boolean EnPlazo(){
        Date hoy = new Date();
        return (hoy.after(fecha_inicio) && hoy.before(fecha_limite));
    }

    public Date getFecha_final() {
        return fecha_final;
    }
    public void setFecha_final(Date fecha_final) {
        this.fecha_final = fecha_final;
    }
    public Date getFecha_limite() {
        return fecha_limite;
    }
    public void setFecha_limite(Date fecha_limite) {
        this.fecha_limite = fecha_limite;
    }
    public BigDecimal getPresupuestototal() {
        return presupuestototal;
    }
    public void setPresupuestototal(BigDecimal presupuestototal) {
        this.presupuestototal = presupuestototal;
    }
    public Date getFecha_inicio() {
        return fecha_inicio;
    }
    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
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

}
