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
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;


/**
 * La clase ProyectosOTPView representa una vista para los proyectos OTP en la aplicación.
 * Extiende la clase VisualizarProyectos y está anotada con varias anotaciones de Vaadin y Spring.
 * 
 * <p>Anotaciones:</p>
 * <ul>
 *   <li>@PageTitle: Establece el título de la página a "Proyectos OTP".</li>
 *   <li>@Route: Mapea la vista a la ruta "proyectosOTP".</li>
 *   <li>@Menu: Añade la vista al menú de la aplicación con un orden y un icono especificados.</li>
 *   <li>@Uses: Indica que la vista utiliza la clase Icon.</li>
 *   <li>@RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_OTP".</li>
 * </ul>
 * 
 * <p>Constructor:</p>
 * <ul>
 *   <li>ProyectosOTPView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService): 
 *       Inicializa la vista con los servicios dados. Si la convocatoria actual no está dentro del 
 *       periodo de evaluación, inicializa la vista con proyectos por evaluar. De lo contrario, muestra 
 *       un mensaje indicando que aún no se puede realizar la evaluación.</li>
 * </ul>
 * 
 * <p>Métodos:</p>
 * <ul>
 *   <li>crearBotonesAcciones(Proyecto proyecto): Crea y devuelve un botón para evaluar el proyecto dado. 
 *       Al hacer clic, navega a la vista "ValoracionTecnica" para el proyecto especificado.</li>
 * </ul>
 */
@PageTitle("Proyectos OTP")
@Route("proyectosOTP")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_OTP")
public class ProyectosOTPView extends VisualizarProyectos {

    public ProyectosOTPView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        super(proyectoService, true);
        Convocatoria convocatoriaActual = convocatoriaService.convocatoriaActual();

        if (!convocatoriaService.convocatoriaActual().enPlazo()) {
            Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.AVALADO),
                    criteriaBuilder.equal(root.get("convocatoria"), convocatoriaActual));

            inicializarVistaProyectos("Proyectos por evaluar", filters);
        } else {
            add(new H1("Aún no se puede realizar la evaluación"));
        }
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Evaluar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ValoracionTecnica/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}
