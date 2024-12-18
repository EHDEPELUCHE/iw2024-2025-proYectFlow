package es.uca.iw.services;

import es.uca.iw.data.EmailSender;
import es.uca.iw.data.Proyecto;
import es.uca.iw.data.Usuario;
import es.uca.iw.repositories.ProyectoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProyectoService {
    private final ProyectoRepository repository;
    private final EmailSender mailSender;

    public ProyectoService(ProyectoRepository repository, EmailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
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
            //MANDAR CORREO
            if(proyecto.getPromotor() != null){
                mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Petición de avalar",
                        "Ha sido ligado al proyecto:\"" + proyecto.getNombre() +"\". Por favor, acceda a la web para ver toda la información y decidir si avala la propuesta.");
            }
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
                if(proyecto.getSolicitante() != null){
                    mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), "Su proyecto NO ha pasado la valoración técnica",
                            "Lo lamentamos, su propuesta:\"" + proyecto.getNombre() +"\" no cumple el mínimo requerido en aspectos técnicos.");
                }
                if(proyecto.getPromotor() != null){
                    mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Su proyecto NO ha pasado la valoración técnica",
                            "Lo lamentamos, su propuesta avalada:\"" + proyecto.getNombre() +"\" no cumple el mínimo requerido en aspectos técnicos.");
                }
                repository.save(proyecto);
                
            }else{
                //30% idoneidad técnica + 30% costes económicos + 40% recursos humanos.
                Double aux = 0.3 * idoneidad.doubleValue() + 0.3 * precio.doubleValue() + 0.4 * horas.doubleValue();
                proyecto.setPuntuacionTecnica(aux);
                proyecto.setEstado(Proyecto.Estado.evaluadoTecnicamente);
                if (proyecto.getPuntuacionTecnica() >= 5){
                    repository.save(proyecto);
                }else{
                    if(proyecto.getSolicitante() != null){
                        mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), "Su proyecto NO ha pasado la valoración técnica",
                                "Lo lamentamos, su propuesta:\"" + proyecto.getNombre() +"\" no cumple el mínimo requerido en aspectos técnicos.");
                    }
                    if(proyecto.getPromotor() != null){
                        mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Su proyecto NO ha pasado la valoración técnica",
                                "Lo lamentamos, su propuesta avalada:\"" + proyecto.getNombre() +"\" no cumple el mínimo requerido en aspectos técnicos.");
                    }
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
            //MANDAR CORREO
            if(proyectoAux.getSolicitante() != null){
                mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), "Su proyecto ha sido avalado",
                        "¡Felicidades! " + proyectoAux.getPromotor().getNombre() + " ha aceptado ser su aval.");
            }
            repository.save(proyectoAux);
        }else{
            proyectoAux.setEstado(Proyecto.Estado.denegado);

            //MANDAR CORREO
            if(proyectoAux.getSolicitante() != null){
                mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), "Su proyecto NO ha sido avalado",
                        "Lo lamentamos, " + proyectoAux.getPromotor().getNombre() + " ha rechazado avalar su propuesta." +
                                " Acceda a nuestra web si aún está en plazo y solicite otro aval.");
            }
            proyectoAux.setPromotor(null);
            repository.save(proyectoAux);
        }
    }

    public void desligarUsuario(Usuario usuario) {
        List<Proyecto> proyectos = repository.findBySolicitante(usuario);
        for (Proyecto proyecto : proyectos) {
            proyecto.setSolicitante(null);
            repository.save(proyecto);
        }
    }
}
