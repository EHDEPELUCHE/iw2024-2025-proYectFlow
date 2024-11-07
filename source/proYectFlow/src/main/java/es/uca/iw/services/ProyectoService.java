package es.uca.iw.services;

import es.uca.iw.data.Proyecto;
import es.uca.iw.data.ProyectoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProyectoService {
    private final ProyectoRepository repository;

    public ProyectoService(ProyectoRepository repository) {
        this.repository = repository;
    }

    public Optional<Proyecto> get(UUID id) {
        return repository.findById(id);
    }

    public Proyecto update(Proyecto entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Proyecto> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Proyecto> list(Pageable pageable, Specification<Proyecto> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}
