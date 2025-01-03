package es.uca.iw.Proyecto;

import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProyectoTest {

    private static Proyecto proyecto;
    private static Usuario promotor;
    private static Usuario solicitante;
    private static Convocatoria convocatoria;

    @BeforeEach
    void setUpBeforeClass() {
        promotor = new Usuario();
        solicitante = new Usuario();
        convocatoria = new Convocatoria();
        proyecto = new Proyecto("Proyecto Test", "Descripcion Test", "Interesados Test", "Alcance Test",
                new BigDecimal("1000.00"), new BigDecimal("500.00"), promotor, solicitante, new Date(), null);
        proyecto.setConvocatoria(convocatoria);
    }

    @Test
    void getNombre() {
        assertEquals("Proyecto Test", proyecto.getNombre());
    }

    @Test
    void setNombre() {
        proyecto.setNombre("Nuevo Nombre");
        assertEquals("Nuevo Nombre", proyecto.getNombre());
    }

    @Test
    void getDescripcion() {
        assertEquals("Descripcion Test", proyecto.getDescripcion());
    }

    @Test
    void setDescripcion() {
        proyecto.setDescripcion("Nueva Descripcion");
        assertEquals("Nueva Descripcion", proyecto.getDescripcion());
    }

    @Test
    void getInteresados() {
        assertEquals("Interesados Test", proyecto.getInteresados());
    }

    @Test
    void setInteresados() {
        proyecto.setInteresados("Nuevos Interesados");
        assertEquals("Nuevos Interesados", proyecto.getInteresados());
    }

    @Test
    void getAlcance() {
        assertEquals("Alcance Test", proyecto.getAlcance());
    }

    @Test
    void setAlcance() {
        proyecto.setAlcance("Nuevo Alcance");
        assertEquals("Nuevo Alcance", proyecto.getAlcance());
    }

    @Test
    void getFechaLimite() {
        assertNotNull(proyecto.getFechaLimite());
    }

    @Test
    void setFechaLimite() {
        Date nuevaFecha = new Date();
        proyecto.setFechaLimite(nuevaFecha);
        assertEquals(nuevaFecha, proyecto.getFechaLimite());
    }

    @Test
    void getFechaSolicitud() {
        assertNotNull(proyecto.getFechaSolicitud());
    }

    @Test
    void setFechaSolicitud() {
        Date nuevaFecha = new Date();
        proyecto.setFechaSolicitud();
        assertEquals(nuevaFecha, proyecto.getFechaSolicitud());
    }

    @Test
    void getCoste() {
        assertEquals(new BigDecimal("1000.00"), proyecto.getCoste());
    }

    @Test
    void getAportacionInicial() {
        assertEquals(new BigDecimal("500.00"), proyecto.getAportacionInicial());
    }

    @Test
    void setCoste() {
        proyecto.setCoste(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("2000.00"), proyecto.getCoste());
    }


    @Test
    void setAportacionInicial() {
        proyecto.setAportacionInicial(new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), proyecto.getAportacionInicial());
    }

    @Test
    void getPuntuacionEstrategica() {
        assertEquals(-1, proyecto.getPuntuacionEstrategica());
    }

    @Test
    void setPuntuacionEstrategica() {
        proyecto.setPuntuacionEstrategica(5.0);
        assertEquals(5.0, proyecto.getPuntuacionEstrategica());
    }

    @Test
    void getPuntuacionTecnica() {
        assertEquals(-1, proyecto.getPuntuacionTecnica());
    }

    @Test
    void setPuntuacionTecnica() {
        proyecto.setPuntuacionTecnica(4.0);
        assertEquals(4.0, proyecto.getPuntuacionTecnica());
    }

    @Test
    void getPuntuacionAval() {
        assertEquals(-1, proyecto.getPuntuacionAval());
    }

    @Test
    void setPuntuacionAval() {
        proyecto.setPuntuacionAval(3.0);
        assertEquals(3.0, proyecto.getPuntuacionAval());
    }

    @Test
    void getMemoria() {
        assertNull(proyecto.getMemoria());
    }

    @Test
    void setMemoria() {
        Blob memoria = null; // Mock or create a Blob instance
        proyecto.setMemoria(memoria);
        assertEquals(memoria, proyecto.getMemoria());
    }

    @Test
    void getPromotor() {
        assertEquals(promotor, proyecto.getPromotor());
    }

    @Test
    void setPromotor() {
        Usuario nuevoPromotor = new Usuario();
        proyecto.setPromotor(nuevoPromotor);
        assertEquals(nuevoPromotor, proyecto.getPromotor());
    }

    @Test
    void getSolicitante() {
        assertEquals(solicitante, proyecto.getSolicitante());
    }

    @Test
    void setSolicitante() {
        Usuario nuevoSolicitante = new Usuario();
        proyecto.setSolicitante(nuevoSolicitante);
        assertEquals(nuevoSolicitante, proyecto.getSolicitante());
    }

    @Test
    void getEstado() {
        assertEquals(Proyecto.Estado.solicitado, proyecto.getEstado());
    }

    @Test
    void setEstado() {
        proyecto.setEstado(Proyecto.Estado.enDesarrollo);
        assertEquals(Proyecto.Estado.enDesarrollo, proyecto.getEstado());
    }

    @Test
    void getConvocatoria() {
        assertEquals(convocatoria, proyecto.getConvocatoria());
    }

    @Test
    void setConvocatoria() {
        Convocatoria nuevaConvocatoria = new Convocatoria();
        proyecto.setConvocatoria(nuevaConvocatoria);
        assertEquals(nuevaConvocatoria, proyecto.getConvocatoria());
    }
}