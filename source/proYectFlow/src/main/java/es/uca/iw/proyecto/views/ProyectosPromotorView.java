package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
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
 * La clase ProyectosPromotorView representa una vista para que los promotores evalúen proyectos.
 * Esta vista es accesible solo para usuarios con el rol "ROLE_PROMOTOR".
 * 
 * <p>Anotaciones:</p>
 * <ul>
 *   <li>@PageTitle: Establece el título de la página a "Avala un proyecto".</li>
 *   <li>@Route: Mapea la vista a la ruta "proyectosPromotor".</li>
 *   <li>@Menu: Añade la vista al menú con orden 5 y un icono.</li>
 *   <li>@Uses: Indica que la vista usa la clase Icon.</li>
 *   <li>@RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_PROMOTOR".</li>
 * </ul>
 * 
 * <p>Constructor:</p>
 * <ul>
 *   <li>ProyectosPromotorView(ProyectoService proyectoService, AuthenticatedUser user): 
 *       Inicializa la vista con una lista de proyectos que están pendientes de ser evaluados por el promotor.
 *       Filtra los proyectos para incluir solo aquellos que están en el estado "SOLICITADO" y pertenecen al promotor autenticado.</li>
 * </ul>
 * 
 * <p>Métodos:</p>
 * <ul>
 *   <li>crearBotonesAcciones(Proyecto proyecto): 
 *       Crea y devuelve un botón para evaluar el proyecto. 
 *       Al hacer clic, navega a la vista "ValoracionPromotor" para el proyecto especificado.</li>
 * </ul>
 */
@PageTitle("Avala un proyecto")
@Route("proyectosPromotor")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_PROMOTOR")
public class ProyectosPromotorView extends VisualizarProyectos {
    public ProyectosPromotorView(ProyectoService proyectoService, AuthenticatedUser user) {
        super(proyectoService, true);
        Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("promotor"), user.get().orElse(null)),
                criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.SOLICITADO)
        );
        inicializarVistaProyectos("Proyectos pendientes de ser avalados", filters);
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Avalar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ValoracionPromotor/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}