package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

/**
 * La clase EstadoProyectosView representa una vista para mostrar el estado de los proyectos del usuario.
 * Extiende la clase VisualizarProyectos y está anotada con @PageTitle, @Route, @Menu y @PermitAll
 * para definir su título, ruta, orden en el menú y permisos de acceso respectivamente.
 * 
 * La vista se inicializa con un ProyectoService y un AuthenticatedUser.
 * Si el usuario está autenticado, filtra los proyectos para mostrar solo aquellos solicitados por el usuario.
 * Si el usuario no está autenticado o no tiene proyectos, muestra un mensaje indicando que no hay proyectos disponibles.
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Estado de mis Proyectos".
 * - @Route: Define la ruta para acceder a esta vista como "estadomisproyectos".
 * - @Menu: Especifica el orden y el icono del elemento del menú.
 * - @PermitAll: Permite el acceso a todos los usuarios.
 * 
 * Constructor:
 * - EstadoProyectosView(ProyectoService proyectoService, AuthenticatedUser userAuthenticated):
 *   Inicializa la vista con el ProyectoService y el AuthenticatedUser dados.
 *   Si el usuario está autenticado, filtra e inicializa la vista con los proyectos del usuario.
 *   Si el usuario no está autenticado, muestra un mensaje indicando que no hay proyectos disponibles.
 * 
 * @param proyectoService El servicio para gestionar proyectos.
 * @param userAuthenticated El usuario autenticado.
 */
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