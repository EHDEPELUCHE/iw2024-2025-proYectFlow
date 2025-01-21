package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;

/**
 * La clase ProyectosjefeView representa una vista para la página "Proyecto jefe".
 * Esta vista es accesible solo para usuarios con el rol "ROLE_OTP".
 * Extiende la clase VisualizarProyectos y se utiliza para mostrar y gestionar proyectos
 * que son dirigidos por el usuario autenticado.
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Proyecto jefe".
 * - @Route: Define la ruta para acceder a esta vista como "proyectosjefe".
 * - @Menu: Añade esta vista al menú de la aplicación con un orden y un icono especificados.
 * - @Uses: Indica que esta vista utiliza la clase Icon.
 * - @RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_OTP".
 * 
 * Constructor:
 * - ProyectosjefeView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService, AuthenticatedUser user):
 *   Inicializa la vista con los servicios proporcionados y el usuario autenticado.
 *   Si la convocatoria actual no está dentro del plazo, filtra y muestra los proyectos
 *   que están en desarrollo, pertenecen a la convocatoria actual y son dirigidos por el usuario autenticado.
 *   De lo contrario, muestra un mensaje indicando que no hay proyectos en la convocatoria actual.
 * 
 * Métodos:
 * - crearBotonesAcciones(Proyecto proyecto): Crea y devuelve un botón para actualizar el avance del proyecto especificado.
 *   El botón navega a la página "GradoAvance" del proyecto cuando se hace clic.
 */
@PageTitle("Proyecto jefe")
@Route("proyectosjefe")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_OTP")
public class ProyectosjefeView extends VisualizarProyectos {

    public ProyectosjefeView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService,  AuthenticatedUser user) {
        super(proyectoService, true);
        Convocatoria convocatoriaActual = convocatoriaService.convocatoriaActual();

        if (!convocatoriaService.convocatoriaActual().enPlazo()) {
            Specification<Proyecto> filtroEvaluados = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.EN_DESARROLLO),
                    criteriaBuilder.equal(root.get("convocatoria"), convocatoriaActual),
                    criteriaBuilder.equal(root.get("jefe"), user.get().orElse(null)));
            inicializarVistaProyectos("Proyectos que diriges", filtroEvaluados);

        } else {
            add(new H1("Aún no hay proyectos en esta convocatoria"));
        }
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Actualizar avance");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("GradoAvance/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}
