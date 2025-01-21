package es.uca.iw.proyecto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz de repositorio para gestionar entidades {@link AlineamientoEstrategico}.
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD y métodos de consulta adicionales.
 * 
 * @see JpaRepository
 */
public interface AlineamientoEstrategicoRepository extends JpaRepository<AlineamientoEstrategico, UUID> {
    public List<AlineamientoEstrategico> findByActivo(Boolean activo);

}
