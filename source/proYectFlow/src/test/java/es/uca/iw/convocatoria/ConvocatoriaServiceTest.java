package es.uca.iw.convocatoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ConvocatoriaServiceTest {

    @Mock
    private ConvocatoriaRepository repository;

    @InjectMocks
    private ConvocatoriaService service;

    private Convocatoria convocatoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        convocatoria = new Convocatoria(BigDecimal.valueOf(40000), Date.valueOf(LocalDate.of(2025, 1, 1)), Date.valueOf(LocalDate.of(2024, 1, 1)), Date.valueOf(LocalDate.of(2025, 9, 1)));
        convocatoria.setActiva(false);
        convocatoria.setFechaFinal(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    void testGuardar() {
        service.guardar(convocatoria);
        verify(repository, times(0)).save(convocatoria);
    }


    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(convocatoria));
        assertEquals(0, service.findAll().size());
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        Convocatoria result = service.findById(id);
        assertNull(result);
    }
}