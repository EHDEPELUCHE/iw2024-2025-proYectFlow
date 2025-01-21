package es.uca.iw.proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.email.EmailSender;
import es.uca.iw.email.EmailService;
import es.uca.iw.usuario.Usuario;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class ProyectoService {
    static final String LAMENTAMOS_PROPUESTA = "Lo lamentamos, su propuesta: ";
    static final String LAMENTAMOS_PROPUESTA_AVALADA = "Lo lamentamos, su propuesta avalada: ";
    private final ProyectoRepository repository;
    private final EmailSender mailSender;
    private final ConvocatoriaService convocatoriaService;
    private final EmailService emailService;

    public ProyectoService(@Valid ProyectoRepository repository, @Valid EmailSender mailSender, @Valid ConvocatoriaService convocatoriaService, @Valid EmailService emailService) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.convocatoriaService = convocatoriaService;
        this.emailService = emailService;
    }

    @Cacheable("Proyecto")
    public Optional<Proyecto> get(@Valid UUID id) {
        return repository.findById(id);
    }

    public void update(@Valid Proyecto entity) {
        repository.save(entity);
    }

    public void delete(@Valid UUID id) {
        repository.deleteById(id);
    }

    public Page<Proyecto> list(@Valid Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Proyecto> list(@Valid Pageable pageable, @Valid Specification<Proyecto> filter) {
        return repository.findAll(filter, pageable);
    }

    public List<Proyecto> list( @Valid Specification<Proyecto> filter) {
        return repository.findAll(filter);
    }

    public List<Proyecto> findByConvocatoria(@Valid Convocatoria convocatoriaVieja) {
        return repository.findByConvocatoria(convocatoriaVieja);
    }

    public int count() {
        return (int) repository.count();
    }

    /**
     * Registra un nuevo proyecto en el sistema.
     *
     * @param proyecto El proyecto a registrar, debe ser válido.
     * @return true si el proyecto se registra correctamente, false si ocurre una violación de integridad de datos.
     * @throws IllegalStateException si no hay ninguna convocatoria activa en el momento del registro.
     *
     * Este método realiza las siguientes acciones:
     * 1. Verifica si hay una convocatoria activa. Si no la hay, lanza una excepción.
     * 2. Asocia el proyecto a la convocatoria activa.
     * 3. Envía un correo electrónico al promotor del proyecto, si existe, notificándole que ha sido ligado al proyecto.
     * 4. Guarda el proyecto en el repositorio.
     */
    public boolean registerProyecto(@Valid Proyecto proyecto) {
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

    /**
     * Establece la valoración técnica de un proyecto.
     * 
     * @param precio      el precio del proyecto, debe ser un valor válido.
     * @param horas       las horas estimadas para el proyecto, debe ser un valor válido.
     * @param idoneidad   la idoneidad técnica del proyecto, debe ser un valor válido.
     * @param proyecto    el proyecto a evaluar.
     * 
     * Si el estado del proyecto no es DENEGADO, se procede a evaluar la idoneidad técnica.
     * Si la idoneidad es 0, el proyecto se deniega y se notifica al solicitante y al promotor.
     * Si la idoneidad es mayor a 0, se calcula la puntuación técnica basada en un 30% de idoneidad,
     * un 30% de costes económicos y un 40% de recursos humanos.
     * Si la puntuación técnica es mayor o igual a 5, se notifica al jefe del proyecto.
     * Si la puntuación técnica es menor a 5, el proyecto se deniega y se notifica al solicitante y al promotor.
     * 
     * @throws IllegalArgumentException si alguno de los parámetros no es válido.
     */
    public void setValoracionTecnica(@Valid int precio, @Valid int horas, @Valid int idoneidad, @Valid Proyecto proyecto) {
        if (proyecto.getEstado() != Proyecto.Estado.DENEGADO) {
            String nopasaTecnica = "Su proyecto NO ha pasado la valoración técnica";
            String nocumpleMinimo = " no cumple el mínimo requerido en aspectos técnicos.";
            if (idoneidad == 0) {
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
                Double aux = 0.3 * idoneidad + 0.3 * precio + 0.4 * horas;
                proyecto.setPuntuacionTecnica(aux);
                proyecto.setEstado(Proyecto.Estado.EVALUADO_TECNICAMENTE);
                if (proyecto.getPuntuacionTecnica() >= 5) {
                    mailSender.sendEmail(proyecto.getJefe().getCorreo(), "Adjuntado como jefe de proyecto",
                        "Ha sido seleccionado como jefe del proyecto:\"" + proyecto.getNombre() + "\" Acceda a la url: " + emailService.getServerUrl() + "proyectosjefe para actualizar información. Si considera que se trata de un error hable con otro miembro de la OTP.");
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

    /**
     * Obtiene el archivo PDF asociado a un proyecto dado su identificador.
     *
     * @param id El identificador único del proyecto.
     * @return Un arreglo de bytes que representa el contenido del archivo PDF.
     *         Si el proyecto no existe, retorna un arreglo de bytes vacío.
     * @throws IOException Si ocurre un error al leer el archivo PDF.
     */
    public byte[] getPdf(@Valid UUID id) throws IOException {
        Optional<Proyecto> optionalProyecto = repository.findById(id);
        if (optionalProyecto.isEmpty()) {
            return new byte[0];
        }
        return optionalProyecto.get().getPdf().readAllBytes();
    }

    /**
     * Establece la valoración del promotor para un proyecto.
     *
     * @param prioridad La prioridad asignada al proyecto.
     * @param avalado Indica si el proyecto ha sido avalado o no.
     * @param proyectoAux El proyecto que se está evaluando.
     *
     * Si el proyecto es avalado, se actualiza su estado a AVALADO, se asigna la puntuación de aval y se envía un correo al solicitante notificándole la aprobación.
     * Si el proyecto no es avalado, se envía un correo al solicitante notificándole el rechazo y se elimina el promotor del proyecto.
     */
    public void setValoracionPromotor(@Valid BigDecimal prioridad, @Valid Boolean avalado, @Valid Proyecto proyectoAux) {
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
                        "Lo lamentamos, " + proyectoAux.getPromotor().getNombre() + " ha rechazado avalar su propuesta: " + proyectoAux.getNombre() +
                                ".\nAcceda a nuestra web si aún está en plazo y solicite otro aval.");
            }
            proyectoAux.setPromotor(null);
            repository.save(proyectoAux);
        }
    }

    public void desligarUsuario(@Valid Usuario usuario) {
        List<Proyecto> proyectos = repository.findBySolicitante(usuario);
        for (Proyecto proyecto : proyectos) {
            proyecto.setSolicitante(null);
            repository.save(proyecto);
        }
    }

    /**
     * Establece la valoración estratégica de un proyecto y actualiza su estado en función de dicha valoración.
     * 
     * @param valoracionE La valoración estratégica del proyecto.
     * @param proyectoAux El proyecto a evaluar.
     * 
     * Si el estado del proyecto no es DENEGADO, se procede a evaluar la valoración estratégica:
     * - Si la valoración estratégica es 0, el proyecto se deniega y se notifica al solicitante y al promotor (si existen).
     * - Si la valoración estratégica es mayor que 0, se calcula la puntuación estratégica como el 70% de la puntuación técnica 
     *   más el 30% de la valoración estratégica. Si la puntuación estratégica es mayor o igual a 5, se guarda el proyecto con 
     *   estado EVALUADO_ESTRATEGICAMENTE. Si es menor a 5, se deniega el proyecto y se notifica al solicitante y al promotor 
     *   (si existen).
     * 
     * En todos los casos, se guarda el estado actualizado del proyecto en el repositorio.
     */
    public void setValoracionEstrategica(@Valid BigDecimal valoracionE, @Valid Proyecto proyectoAux) {
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

    /**
     * Desarrolla o no un proyecto basado en el valor de sedesarrolla.
     * Si sedesarrolla es verdadero, el estado del proyecto se establece en EN_DESARROLLO y se envían correos electrónicos
     * al solicitante y al promotor (si existen) notificándoles que el proyecto se va a realizar.
     * Si sedesarrolla es falso, el estado del proyecto se establece en NO_EN_DESARROLLO y se envían correos electrónicos
     * al solicitante y al promotor (si existen) notificándoles que el proyecto no se va a realizar.
     *
     * @param proyecto El proyecto a desarrollar o no.
     * @param sedesarrolla Booleano que indica si el proyecto se desarrollará (true) o no (false).
     */
    @Transactional
    public void desarrollar(@Valid Proyecto proyecto, @Valid Boolean sedesarrolla) {
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