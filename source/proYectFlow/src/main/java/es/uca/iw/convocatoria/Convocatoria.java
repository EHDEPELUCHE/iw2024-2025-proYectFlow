package es.uca.iw.convocatoria;

import es.uca.iw.global.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
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

    public Convocatoria(BigDecimal presupuesto, Date fechaLimite, Date fechaInicio, Date fechaFinal) {
        this.presupuestototal = presupuesto;
        this.presupuestorestante = presupuesto;
        this.fechaInicio = fechaInicio;
        setFechaLimite(fechaLimite);
        setFechaFinal(fechaFinal);
        setNombre();
    }

    public Convocatoria() {}

    public boolean enPlazo(){
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
    public String getNombre() {return nombre;}
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

    @Override
    public int hashCode() {
        return Objects.hash(presupuestototal, fechaInicio, fechaFinal);
    }
}
