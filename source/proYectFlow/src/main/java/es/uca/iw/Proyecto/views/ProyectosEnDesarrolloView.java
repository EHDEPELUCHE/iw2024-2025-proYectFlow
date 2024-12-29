package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.html.H1;
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
        if(convocatoriaActual != null) {
            Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.enDesarrollo),
                    criteriaBuilder.equal(root.get("convocatoria"), convocatoriaActual)
            );
            inicializarVistaProyectos("Proyectos que se están desarrollando actualmente", filters);
        }else{
            add(new H1("No existe una proyecto en desarrollo"));
        }

    }
}