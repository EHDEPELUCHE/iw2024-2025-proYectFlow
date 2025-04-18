package es.uca.iw.proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.email.EmailSender;
import es.uca.iw.global.Roles;
import es.uca.iw.usuario.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProyectoServiceTest {
    @Mock
    private ProyectoRepository repository;

    @Mock
    private EmailSender mailSender;

    @Mock
    private ConvocatoriaService convocatoriaService;

    @InjectMocks
    private ProyectoService proyectoService;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("spring.profiles.active", "test");

    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        convocatoriaService = mock(ConvocatoriaService.class);
        when(convocatoriaService.convocatoriaActual()).thenReturn(new Convocatoria());
    }

    @Test
    void get() {
        UUID id = UUID.randomUUID();
        Proyecto proyecto = new Proyecto();
        when(repository.findById(id)).thenReturn(Optional.of(proyecto));

        Optional<Proyecto> result = proyectoService.get(id);

        assertTrue(result.isPresent());
        assertEquals(proyecto, result.get());
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        doNothing().when(repository).deleteById(id);

        proyectoService.delete(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void list() {
        Page<Proyecto> page = new PageImpl<>(Collections.singletonList(new Proyecto()));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Proyecto> result = proyectoService.list(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void listWithFilter() {
        Page<Proyecto> page = new PageImpl<>(Collections.singletonList(new Proyecto()));
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
        Specification<Proyecto> filter = (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        Page<Proyecto> result = proyectoService.list(PageRequest.of(0, 10), filter);

        assertEquals(1, result.getTotalElements());
    }


    @Test
    void setValoracionTecnica() {
        Proyecto proyecto = new Proyecto();
        proyecto.setEstado(Proyecto.Estado.EN_DESARROLLO);
        Usuario jefe = new Usuario("usu1nombre", "usu1", "usu1apellido", "usu1email", "usu1pass", Roles.OTP);
        proyecto.setJefe(jefe);
        when(repository.save(proyecto)).thenReturn(proyecto);

        proyectoService.setValoracionTecnica(100, 100, 100, proyecto);

        assertEquals(Proyecto.Estado.EVALUADO_TECNICAMENTE, proyecto.getEstado());
        verify(repository, times(1)).save(proyecto);
    }

    @Test
    void setValoracionPromotor() {
        Proyecto proyecto = new Proyecto();
        Usuario promotor = new Usuario();
        promotor.setCorreo("test@example.com");
        proyecto.setPromotor(promotor);
        when(repository.save(proyecto)).thenReturn(proyecto);

        proyectoService.setValoracionPromotor(BigDecimal.valueOf(10), true, proyecto);

        assertEquals(Proyecto.Estado.AVALADO, proyecto.getEstado());
        verify(repository, times(1)).save(proyecto);
    }

    @Test
    void setValoracionEstrategica() {
        Proyecto proyecto = new Proyecto();
        proyecto.setPuntuacionTecnica(7);
        proyecto.setEstado(Proyecto.Estado.EVALUADO_TECNICAMENTE);
        when(repository.save(proyecto)).thenReturn(proyecto);

        proyectoService.setValoracionEstrategica(BigDecimal.valueOf(10), proyecto);

        assertEquals(Proyecto.Estado.EVALUADO_ESTRATEGICAMENTE, proyecto.getEstado());
        verify(repository, times(1)).save(proyecto);
    }

    @Test
    void desarrollar() {
        Proyecto proyecto = new Proyecto();
        proyecto.setEstado(Proyecto.Estado.EN_DESARROLLO);
        when(repository.save(proyecto)).thenReturn(proyecto);

        proyectoService.desarrollar(proyecto, true);

        assertEquals(Proyecto.Estado.EN_DESARROLLO, proyecto.getEstado());
        verify(repository, times(1)).save(proyecto);
    }
}