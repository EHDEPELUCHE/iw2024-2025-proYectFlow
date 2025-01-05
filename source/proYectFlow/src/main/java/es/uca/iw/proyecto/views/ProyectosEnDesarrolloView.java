package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Proyectos en Desarrollo")
@Route("ProyectosDesarrollo")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@AnonymousAllowed
public class ProyectosEnDesarrolloView extends VisualizarProyectos {
    public ProyectosEnDesarrolloView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        super(proyectoService, false);
        Convocatoria convocatoriaActual = convocatoriaService.convocatoriaActual();
        if (convocatoriaActual != null) {
            Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.enDesarrollo),
                    criteriaBuilder.equal(root.get("convocatoria"), convocatoriaActual)
            );
            inicializarVistaProyectos("Proyectos que se están desarrollando actualmente", filters);
        } else {
            H1 heading = new H1("No existe ningún proyecto en desarrollo actualmente.");
            heading.getElement().setAttribute("role", "alert");
            add(heading);
        }
    }
}