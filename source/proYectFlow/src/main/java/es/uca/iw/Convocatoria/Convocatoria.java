package es.uca.iw.Convocatoria;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Convocatoria extends AbstractEntity {

    @Column(unique = true, nullable = true)
    String nombre;

    @NotNull(message = "Por favor, rellene el campo requerido")
    @Column(nullable = false)
    BigDecimal presupuestototal;

    BigDecimal presupuestorestante;

    Date fecha_limite;

    @Column(nullable = false)
    Date fecha_inicio;

    @Column(nullable = false)
    Date fecha_final;

    Boolean activa;

    public Convocatoria(BigDecimal presupuesto, Date fecha_limite, Date fecha_inicio, Date fecha_final) {
        this.presupuestototal = presupuesto;
        this.fecha_limite = fecha_limite;
        this.fecha_inicio = fecha_inicio;
        this.presupuestorestante = presupuesto;
        this.fecha_final = fecha_final;
        setNombre();
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
        setNombre();
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
    public String getNombre() {return nombre;}
    private void setNombre() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        dateFormat.format(fecha_inicio);
        dateFormat.format(fecha_final);

        this.nombre = "Convocatoria " + dateFormat.format(fecha_inicio) + "- " + dateFormat.format(fecha_final);
    }
}
