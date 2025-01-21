package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;

/**
 * Esta clase de vista representa la página "Proyectos avalados" para usuarios con el rol "ROLE_PROMOTOR".
 * Extiende la clase VisualizarProyectos para mostrar proyectos que han sido avalados por el promotor.
 * 
 * <p>Anotaciones:</p>
 * <ul>
 *   <li>@PageTitle: Establece el título de la página como "Proyectos avalados".</li>
 *   <li>@Route: Define la ruta para acceder a esta vista como "proyectosAvaladosPromotor".</li>
 *   <li>@Menu: Añade esta vista al menú con un orden de 5 y un icono.</li>
 *   <li>@Uses: Indica que esta vista utiliza la clase Icon.</li>
 *   <li>@RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_PROMOTOR".</li>
 * </ul>
 * 
 * <p>Constructor:</p>
 * <ul>
 *   <li>ProyectosAvaladosPromotor(ProyectoService proyectoService, AuthenticatedUser user): 
 *       Inicializa la vista con el ProyectoService y AuthenticatedUser proporcionados. 
 *       Configura filtros para mostrar proyectos donde el promotor es el usuario autenticado 
 *       y el estado del proyecto no es "SOLICITADO".</li>
 * </ul>
 * 
 * @param proyectoService El servicio utilizado para gestionar proyectos.
 * @param user El usuario autenticado.
 */
@PageTitle("Proyectos avalados")
@Route("proyectosAvaladosPromotor")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_PROMOTOR")
public class ProyectosAvaladosPromotor extends VisualizarProyectos {
    public ProyectosAvaladosPromotor(ProyectoService proyectoService, AuthenticatedUser user) {
        super(proyectoService, false);
        Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
            criteriaBuilder.equal(root.get("promotor"), user.get().orElse(null)),
            criteriaBuilder.notEqual(root.get("estado"), Proyecto.Estado.SOLICITADO)
        );
        inicializarVistaProyectos("Proyectos avalados", filters);
    }
}