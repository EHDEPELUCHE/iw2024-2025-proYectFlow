package es.uca.iw.proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.email.EmailSender;
import es.uca.iw.usuario.Usuario;
import org.springframework.cache.annotation.Cacheable;
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
    static final String LAMENTAMOS_PROPUESTA = "Lo lamentamos, su propuesta: ";
    static final String LAMENTAMOS_PROPUESTA_AVALADA = "Lo lamentamos, su propuesta avalada: ";
    private final ProyectoRepository repository;
    private final EmailSender mailSender;
    private final ConvocatoriaService convocatoriaService;

    public ProyectoService(ProyectoRepository repository, EmailSender mailSender, ConvocatoriaService convocatoriaService) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.convocatoriaService = convocatoriaService;
    }

    @Cacheable("Proyecto")
    public Optional<Proyecto> get(UUID id) {
        return repository.findById(id);
    }

    public void update(Proyecto entity) {
        repository.save(entity);
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
            //Convocatoria activa
            Convocatoria convocatoriaActiva = convocatoriaService.convocatoriaActual();
            if (convocatoriaActiva == null) {
                throw new IllegalStateException("No hay ninguna convocatoria activa en este momento.");
            }
            proyecto.setConvocatoria(convocatoriaActiva);
            //MANDAR CORREO
            if (proyecto.getPromotor() != null) {
                mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Petición de avalar",
                        "Ha sido ligado al proyecto:\"" + proyecto.getNombre() + "\". Por favor, acceda a la web para ver toda la información y decidir si avala la propuesta.");
            }
            repository.save(proyecto);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public void setValoracionTecnica(BigDecimal precio, BigDecimal horas, BigDecimal idoneidad, Proyecto proyecto) {
        if (proyecto.getEstado() != Proyecto.Estado.DENEGADO) {
            String nopasaTecnica = "Su proyecto NO ha pasado la valoración técnica";
            String nocumpleMinimo = " no cumple el mínimo requerido en aspectos técnicos.";
            if (idoneidad.doubleValue() == 0) {
                proyecto.setEstado(Proyecto.Estado.DENEGADO);
                proyecto.setPuntuacionTecnica(0);
                if (proyecto.getSolicitante() != null) {
                    mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), nopasaTecnica,
                            LAMENTAMOS_PROPUESTA + "\"" + proyecto.getNombre() + "\"" + nocumpleMinimo);
                }
                if (proyecto.getPromotor() != null) {
                    mailSender.sendEmail(proyecto.getPromotor().getCorreo(), nopasaTecnica,
                            LAMENTAMOS_PROPUESTA_AVALADA + "\"" + proyecto.getNombre() + "\"" + nocumpleMinimo);
                }
                repository.save(proyecto);
            } else {
                //30% idoneidad técnica + 30% costes económicos + 40% recursos humanos.
                Double aux = 0.3 * idoneidad.doubleValue() + 0.3 * precio.doubleValue() + 0.4 * horas.doubleValue();
                proyecto.setPuntuacionTecnica(aux);
                proyecto.setEstado(Proyecto.Estado.EVALUADO_TECNICAMENTE);
                if (proyecto.getPuntuacionTecnica() >= 5) {
                    repository.save(proyecto);
                } else {
                    if (proyecto.getSolicitante() != null) {
                        mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), nopasaTecnica,
                                LAMENTAMOS_PROPUESTA + "\"" + proyecto.getNombre() + "\"" + nocumpleMinimo);
                    }
                    if (proyecto.getPromotor() != null) {
                        mailSender.sendEmail(proyecto.getPromotor().getCorreo(), nopasaTecnica,
                                LAMENTAMOS_PROPUESTA_AVALADA + "\"" + proyecto.getNombre() + "\"" + nocumpleMinimo);
                    }
                    proyecto.setEstado(Proyecto.Estado.DENEGADO);
                    repository.save(proyecto);
                }
            }
        }
    }

    public byte[] getPdf(UUID id) throws IOException {
        Optional<Proyecto> optionalProyecto = repository.findById(id);
        if (optionalProyecto.isEmpty()) {
            return new byte[0];
        }
        return optionalProyecto.get().getPdf().readAllBytes();
    }

    public void setValoracionPromotor(BigDecimal prioridad, Boolean avalado, Proyecto proyectoAux) {
        if (Boolean.TRUE.equals(avalado)) {
            proyectoAux.setEstado(Proyecto.Estado.AVALADO);
            proyectoAux.setPuntuacionAval(prioridad.doubleValue());
            //MANDAR CORREO
            if (proyectoAux.getSolicitante() != null) {
                mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), "Su proyecto ha sido avalado",
                        "¡Felicidades! " + proyectoAux.getPromotor().getNombre() + " ha aceptado ser su aval en el proyecto: " + proyectoAux.getNombre() + 
                        ".\nAcceda a nuestra web para ver más información.");
            }
            repository.save(proyectoAux);
        } else {
            //MANDAR CORREO
            if (proyectoAux.getSolicitante() != null) {
                mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), "Su proyecto NO ha sido avalado",
                        "Lo lamentamos, " + proyectoAux.getPromotor().getNombre() + " ha rechazado avalar su propuesta: " + proyectoAux.getNombre()  +
                        ".\nAcceda a nuestra web si aún está en plazo y solicite otro aval.");
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

    public void setValoracionEstrategica(BigDecimal valoracionE, Proyecto proyectoAux) {
        //En puntuacionEstrategica guardaremos la valoración final
        if (proyectoAux.getEstado() != Proyecto.Estado.DENEGADO) {
            String nopasaEstrategica = "Su proyecto NO ha pasado la valoración estratégica";
            String nocumpleObjetivos = " no cumple los objetivos que buscamos.";
            if (valoracionE.doubleValue() == 0) {
                proyectoAux.setEstado(Proyecto.Estado.DENEGADO);
                proyectoAux.setPuntuacionEstrategica(0);
                if (proyectoAux.getSolicitante() != null) {
                    mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), nopasaEstrategica,
                            LAMENTAMOS_PROPUESTA + "\"" + proyectoAux.getNombre() + "\"" + nocumpleObjetivos);
                }
                if (proyectoAux.getPromotor() != null) {
                    mailSender.sendEmail(proyectoAux.getPromotor().getCorreo(), nopasaEstrategica,
                            LAMENTAMOS_PROPUESTA_AVALADA + "\"" + proyectoAux.getNombre() + "\"" + nocumpleObjetivos);
                }
                repository.save(proyectoAux);
            } else {
                //70% valoración OTP + 30% valoración CIO.
                Double aux = 0.7 * proyectoAux.getPuntuacionTecnica() + 0.3 * valoracionE.doubleValue();
                proyectoAux.setPuntuacionEstrategica(aux);
                proyectoAux.setEstado(Proyecto.Estado.EVALUADO_ESTRATEGICAMENTE);
                if (proyectoAux.getPuntuacionEstrategica() >= 5) {
                    repository.save(proyectoAux);
                } else {
                    if (proyectoAux.getSolicitante() != null) {
                        mailSender.sendEmail(proyectoAux.getSolicitante().getCorreo(), nopasaEstrategica,
                                LAMENTAMOS_PROPUESTA + "\"" + proyectoAux.getNombre() + "\"" + nocumpleObjetivos);
                    }
                    if (proyectoAux.getPromotor() != null) {
                        mailSender.sendEmail(proyectoAux.getPromotor().getCorreo(), nopasaEstrategica,
                                LAMENTAMOS_PROPUESTA_AVALADA + "\"" + proyectoAux.getNombre() + "\"" + nocumpleObjetivos);
                    }
                    proyectoAux.setEstado(Proyecto.Estado.DENEGADO);
                    repository.save(proyectoAux);
                }
            }
        }
    }

    public void desarrollar(Proyecto proyecto, Boolean sedesarrolla) {
        if (Boolean.TRUE.equals(sedesarrolla)) {
            proyecto.setEstado(Proyecto.Estado.EN_DESARROLLO);
            if (proyecto.getSolicitante() != null) {
                mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), "Su proyecto se va a realizar",
                        "Felicidades, su propuesta:\"" + proyecto.getNombre() + "\" va a realizarse.");
            }
            if (proyecto.getPromotor() != null) {
                mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Su proyecto avalado se va a realizar",
                        "Felicidades, su propuesta avalada:\"" + proyecto.getNombre() + "\" será realizada.");
            }
        } else {
            proyecto.setEstado(Proyecto.Estado.NO_EN_DESARROLLO);
            if (proyecto.getSolicitante() != null) {
                mailSender.sendEmail(proyecto.getSolicitante().getCorreo(), "Su proyecto NO se va a realizar",
                        LAMENTAMOS_PROPUESTA + "\"" + proyecto.getNombre() + "\" no va a realizarse por falta de presupuesto." +
                                " Inténtelo el próximo año, apreciamos mucho sus propuestas");
            }
            if (proyecto.getPromotor() != null) {
                mailSender.sendEmail(proyecto.getPromotor().getCorreo(), "Su proyecto avalado NO se va a realizar",
                        LAMENTAMOS_PROPUESTA_AVALADA + "\"" + proyecto.getNombre() + "\" no será realizada por falta de presupuesto.");
            }
        }
        repository.save(proyecto);
    }
}
