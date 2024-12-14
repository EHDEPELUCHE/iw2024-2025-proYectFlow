package es.uca.iw.services;

import es.uca.iw.data.Proyecto;
import es.uca.iw.repositories.ProyectoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
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

    public boolean registerProyecto(Proyecto proyecto) {

        try {
            repository.save(proyecto);

            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }


    public void setValoracionTecnica(BigDecimal precio, BigDecimal horas, BigDecimal idoneidad, Proyecto proyecto) {
        if(proyecto.getEstado() != Proyecto.Estado.denegado){
            if(idoneidad.doubleValue() == 0){
                proyecto.setEstado(Proyecto.Estado.denegado);
                proyecto.setPuntuacionTecnica(0);
                repository.save(proyecto);
                
            }else{
                //30% idoneidad técnica + 30% costes económicos + 40% recursos humanos.
                Double aux = 0.3 * idoneidad.doubleValue() + 0.3 * precio.doubleValue() + 0.4 * horas.doubleValue();
                proyecto.setPuntuacionTecnica(aux);
                proyecto.setEstado(Proyecto.Estado.evaluadoTecnicamente);
                if (proyecto.getPuntuacionTecnica() >= 5){
                    repository.save(proyecto);
                }else{
                    proyecto.setEstado(Proyecto.Estado.denegado);
                    repository.save(proyecto);
                }
            }
            
            
        }

    }

    public byte[] getPdf(UUID id) throws IOException {
        Proyecto proyecto = repository.findById(id).get();
        return proyecto.getPdf().readAllBytes();
    }


    public void setValoracionPromotor(BigDecimal prioridad, Boolean avalado, Proyecto proyectoAux) {
        if(avalado){
            proyectoAux.setEstado(Proyecto.Estado.avalado);
            proyectoAux.setPuntuacionAval(prioridad.doubleValue());
            repository.save(proyectoAux);
        }else{
            proyectoAux.setEstado(Proyecto.Estado.denegado);
            repository.save(proyectoAux);
        }
    }
}
