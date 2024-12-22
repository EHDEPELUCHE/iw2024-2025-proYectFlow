package es.uca.iw.Proyecto.views;

import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Proyecto.VisualizarProyectos;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Proyectos en Desarrollo")
@Route("ProyectosDesarrollo")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@AnonymousAllowed
public class ProyectosEnDesarrolloView extends VisualizarProyectos {
    public ProyectosEnDesarrolloView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        super(proyectoService,false);
        Convocatoria convocatoriaActual = convocatoriaService.ConvocatoriaActual();

        Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
            criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.enDesarrollo),
            criteriaBuilder.greaterThanOrEqualTo(root.get("fechaSolicitud"), convocatoriaActual.getFecha_inicio()),
            criteriaBuilder.lessThanOrEqualTo(root.get("fechaSolicitud"), convocatoriaActual.getFecha_final())
        );
        inicializarVistaProyectos("Proyectos que se están desarrollando actualmente", filters);
    }
}