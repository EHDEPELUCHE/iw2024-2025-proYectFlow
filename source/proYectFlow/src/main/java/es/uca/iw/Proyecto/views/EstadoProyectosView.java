package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Proyecto.VisualizarProyectos;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@PageTitle("Estado de mis Proyectos")
@Route("estadomisproyectos")
@Menu(order = 2, icon = "line-awesome/svg/archive-solid.svg")
@PermitAll
public class EstadoProyectosView extends VisualizarProyectos {
    public EstadoProyectosView(ProyectoService proyectoService, AuthenticatedUser userAuthenticated) {
        super(proyectoService, true);

        Optional<Usuario> usuarioOptional = userAuthenticated.get();
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            Specification<Proyecto> filters = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("solicitante"), usuario);
            inicializarVistaProyectos("Mis proyectos", filters);
        } else {
            add(new H1("No tienes proyectos disponibles."));
        }
    }
}