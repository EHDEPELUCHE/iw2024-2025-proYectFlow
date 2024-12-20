package es.uca.iw.Proyecto;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlineamientoEstrategicoService {
    private final AlineamientoEstrategicoRepository repository;

    public AlineamientoEstrategicoService(AlineamientoEstrategicoRepository repository) {
        this.repository = repository;
    }

    public List<AlineamientoEstrategico> findAll() {
        return repository.findAll();
    }
    public AlineamientoEstrategico findById(UUID id) {
        return repository.findById(id).orElse(null);
    }
    @Transactional
    public AlineamientoEstrategico guardar(AlineamientoEstrategico alineamiento) {
        return repository.save(alineamiento) ;
    }
}
