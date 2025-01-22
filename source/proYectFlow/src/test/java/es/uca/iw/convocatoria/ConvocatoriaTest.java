package es.uca.iw.convocatoria;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;

class ConvocatoriaTest {

    private Convocatoria convocatoria;
    private Date fechaInicio;
    private Date fechaLimite;
    private Date fechaFinal;

    @BeforeEach
    void setUp() {
        fechaInicio = new Date(System.currentTimeMillis() - 100000);
        fechaLimite = new Date(System.currentTimeMillis() + 100000);
        fechaFinal = new Date(System.currentTimeMillis() + 200000);
        convocatoria = new Convocatoria(new BigDecimal("1000"), fechaLimite, fechaInicio, fechaFinal, 800);
    }

    @Test
    void testEnPlazo() {
        assertTrue(convocatoria.enPlazo());
    }

    @Test
    void testSetFechaFinal() {
        Date nuevaFechaFinal = new Date(System.currentTimeMillis() + 300000);
        convocatoria.setFechaFinal(nuevaFechaFinal);
        assertEquals(nuevaFechaFinal, convocatoria.getFechaFinal());
    }

    @Test
    void testSetFechaFinalThrowsException() {
        Date nuevaFechaFinal = new Date(System.currentTimeMillis() - 300000);
        assertThrows(IllegalArgumentException.class, () -> convocatoria.setFechaFinal(nuevaFechaFinal));
    }

    @Test
    void testSetFechaLimite() {
        Date nuevaFechaLimite = new Date(System.currentTimeMillis() + 150000);
        convocatoria.setFechaLimite(nuevaFechaLimite);
        assertEquals(nuevaFechaLimite, convocatoria.getFechaLimite());
    }

    @Test
    void testSetFechaLimiteThrowsException() {
        Date nuevaFechaLimite = new Date(System.currentTimeMillis() - 150000);
        assertThrows(IllegalArgumentException.class, () -> convocatoria.setFechaLimite(nuevaFechaLimite));
    }

    @Test
    void testSetPresupuestototal() {
        BigDecimal nuevoPresupuesto = new BigDecimal("2000");
        convocatoria.setPresupuestototal(nuevoPresupuesto);
        assertEquals(nuevoPresupuesto, convocatoria.getPresupuestototal());
    }

    @Test
    void testSetPresupuestorestante() {
        BigDecimal nuevoPresupuestoRestante = new BigDecimal("500");
        convocatoria.setPresupuestorestante(nuevoPresupuestoRestante);
        assertEquals(nuevoPresupuestoRestante, convocatoria.getPresupuestorestante());
    }

    @Test
    void testSetActiva() {
        convocatoria.setActiva(true);
        assertTrue(convocatoria.getActiva());
    }

    @Test
    void testEquals() {
        Convocatoria otraConvocatoria = new Convocatoria(new BigDecimal("1000"), fechaLimite, fechaInicio, fechaFinal, 800);
        assertEquals(convocatoria, otraConvocatoria);
    }

    @Test
    void testHashCode() {
        Convocatoria otraConvocatoria = new Convocatoria(new BigDecimal("1000"), fechaLimite, fechaInicio, fechaFinal,800);
        assertEquals(convocatoria.hashCode(), otraConvocatoria.hashCode());
    }
}