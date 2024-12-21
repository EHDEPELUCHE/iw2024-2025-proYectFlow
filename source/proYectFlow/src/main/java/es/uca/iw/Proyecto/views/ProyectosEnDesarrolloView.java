package es.uca.iw.Proyecto.views;

import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Proyecto.VisualizarProyectos;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Proyectos en Desarrollo")
@Route("ProyectosDesarrollo")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@AnonymousAllowed
public class ProyectosEnDesarrolloView extends VisualizarProyectos {
    public ProyectosEnDesarrolloView(ProyectoService proyectoService) {
        super(proyectoService);

        Specification<Proyecto> filters = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.enDesarrollo);
        inicializarVistaProyectos("Proyectos que se están desarrollando actualmente", filters);
    }
}